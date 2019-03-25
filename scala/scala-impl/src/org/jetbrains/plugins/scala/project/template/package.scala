package org.jetbrains.plugins.scala
package project

import java.io._

import com.intellij.execution.process.{OSProcessHandler, ProcessAdapter, ProcessEvent}
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.util.{PathUtil, net}

import scala.collection.JavaConverters

/**
 * @author Pavel Fatin
 */
package object template {

  private val DefaultCommands = Array(
    "java",
    "-Djline.terminal=jline.UnsupportedTerminal",
    "-Dsbt.log.noformat=true"
  )

  class DownloadProcessAdapter(private val progressManager: ProgressManager) extends ProcessAdapter {

    private val builder = StringBuilder.newBuilder

    override def onTextAvailable(event: ProcessEvent, outputType: Key[_]): Unit = {
      val text = event.getText

      for {
        manager <- Option(progressManager)
        if manager.hasProgressIndicator

        indicator = manager.getProgressIndicator
      } indicator.setText(text)

      builder ++= text
    }

    def text: String = builder.toString
  }

  def createTempSbtProject(version: String,
                           listener: DownloadProcessAdapter)
                          (preUpdateCommands: Seq[String] = Seq.empty,
                           postUpdateCommands: Seq[String] = Seq.empty): Unit =
    usingTempFile("sbt-commands") { file =>
      writeLinesTo(file)(
        (s"""set scalaVersion := "$version"""" +: preUpdateCommands :+ "updateClassifiers") ++
          postUpdateCommands: _*
      )

      usingTempDirectory("sbt-project") { dir =>
        val process = Runtime.getRuntime.exec(
          DefaultCommands ++ vmOptions ++ launcherOptions(file.getAbsolutePath),
          null,
          dir
        )

        val handler = new OSProcessHandler(process, "sbt-based downloader", null)
        handler.addProcessListener(listener)
        handler.startNotify()
        handler.waitFor()

        val text = listener.text
        process.exitValue match {
          case 0 => text
          case _ => throw new RuntimeException(text)
        }
      }
    }


  def using[A <: Closeable, B](resource: A)(block: A => B): B = {
    try {
      block(resource)
    } finally {
      resource.close()
    }
  }

  def usingTempFile[T](prefix: String, suffix: Option[String] = None)(block: File => T): T = {
    val file = FileUtil.createTempFile(prefix, suffix.orNull, true)
    try {
      block(file)
    } finally {
      file.delete()
    }
  }

  def usingTempDirectory[T](prefix: String, suffix: Option[String] = None)(block: File => T): T = {
    val directory = FileUtil.createTempDirectory(prefix, suffix.orNull, true)
    try {
      block(directory)
    } finally {
      FileUtil.delete(directory)
    }
  }

  def writeLinesTo(file: File)
                  (lines: String*): Unit = {
    using(new PrintWriter(new FileWriter(file))) { writer =>
      lines.foreach(writer.println)
      writer.flush()
    }
  }

  implicit class FileExt(val delegate: File) extends AnyVal {
    def /(path: String): File = new File(delegate, path)

    def /(paths: Seq[String]): File = paths.foldLeft(delegate)(_ / _)

    def parent: Option[File] = Option(delegate.getParentFile)

    def children: Seq[File] = Option(delegate.listFiles).map(_.toSeq).getOrElse(Seq.empty)

    def directories: Seq[File] = children.filter(_.isDirectory)

    def files: Seq[File] = children.filter(_.isFile)

    def findByName(name: String): Option[File] = children.find(_.getName == name)

    def allFiles: Stream[File] = {
      val (files, directories) = children.toStream.span(_.isFile)
      files #::: directories.flatMap(_.allFiles)
    }

    def toLibraryRootURL: String = VfsUtil.getUrlForLibraryRoot(delegate)
  }

  private[this] def launcherOptions(path: String) =
    jarWith.getParentFile.getParentFile / "launcher" / "sbt-launch.jar" match {
      case launcher if launcher.exists => Seq("-jar", launcher.getAbsolutePath, "< " + path)
      case launcher => throw new FileNotFoundException(launcher.getPath)
    }

  private[this] def jarWith = {
    val aClass = getClass
    PathUtil.getJarPathForClass(aClass) match {
      case null => throw new RuntimeException("Jar file not found for class: " + aClass.getName)
      case pathname => new File(pathname)
    }
  }

  private[this] def vmOptions = {
    import JavaConverters._
    net.HttpConfigurable.getInstance
      .getJvmProperties(false, null)
      .asScala
      .map { pair =>
        "-D" + pair.getFirst + "=" + pair.getSecond
      }
  }
}
