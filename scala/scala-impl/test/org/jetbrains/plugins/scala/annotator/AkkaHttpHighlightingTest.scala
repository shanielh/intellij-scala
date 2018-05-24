package org.jetbrains.plugins.scala.annotator

import org.jetbrains.plugins.scala.DependencyManagerBase._
import org.jetbrains.plugins.scala.base.libraryLoaders.{IvyManagedLoader, LibraryLoader}
import org.jetbrains.plugins.scala.debugger.{ScalaVersion, Scala_2_12}

/**
  * Nikolay.Tropin
  * 23-May-18
  */
class AkkaHttpHighlightingTest extends ScalaHighlightingTestBase {

  override implicit val version: ScalaVersion = Scala_2_12
  private val akkaHttpVersion = "10.0.11"
  private val akkaVersion     = "2.5.8"

  override def librariesLoaders: Seq[LibraryLoader] =
    super.librariesLoaders :+
      IvyManagedLoader(
        "com.typesafe.akka" %% "akka-http"      % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-actor"     % akkaVersion
      )

  def testSCL11470(): Unit = {
    assertNothing(errorsFromScalaCode(
      """
        |import akka.http.scaladsl.server.Route
        |import akka.http.scaladsl.server.Directives._
        |import akka.http.scaladsl.server.directives.Credentials
        |import akka.http.scaladsl.settings.RoutingSettings
        |
        |class Server {
        |  implicit val routingSettings: RoutingSettings = RoutingSettings("")
        |
        |  val routes =
        |    Route.seal {
        |      path("secured") {
        |        authenticateBasic[String]("s", authenticator) { k =>
        |          complete(s"$k")
        |        }
        |      }
        |    }
        |
        |  def authenticator(credentials: Credentials): Option[String] = None
        |}
      """.stripMargin))
  }
}
