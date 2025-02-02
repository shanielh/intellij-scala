package org.jetbrains.plugins.scala.project.sdkdetect.repository;

import dev.dirs.ProjectDirectories;

import java.io.File;
import java.io.IOException;

/**
 * Computes Coursier's directories according to the standard
 * defined by operating system Coursier is running on.
 * Copied from coursier/modules/paths/src/main/java/coursier/paths/CoursierPaths.java
 *
 * @implNote If more paths e. g. for configuration or application data is required,
 * use {@link #coursierDirectories} and do not roll your own logic.
 */
public final class CoursierPaths {
    private CoursierPaths() {
        throw new Error();
    }

    private static final Object coursierDirectoriesLock = new Object();
    private static ProjectDirectories coursierDirectories0;

    private static final Object cacheDirectoryLock = new Object();
    private static volatile File cacheDirectory0 = null;

    private static final Object configDirectoryLock = new Object();
    private static volatile File configDirectory0 = null;

    private static final Object dataLocalDirectoryLock = new Object();
    private static volatile File dataLocalDirectory0 = null;

    // TODO After switching to nio, that logic can be unit tested with mock filesystems.

    private static String computeCacheDirectory() throws IOException {
        String path = System.getenv("COURSIER_CACHE");

        if (path == null)
            path = System.getProperty("coursier.cache");

        if (path != null)
          return path;

        File baseXdgDir = new File(coursierDirectories().cacheDir);
        File xdgDir = new File(baseXdgDir, "v1");
        String xdgPath = xdgDir.getAbsolutePath();

        if (baseXdgDir.isDirectory())
            path = xdgPath;

        if (path == null) {
            File coursierDotFile = new File(System.getProperty("user.home") + "/.coursier");
            if (coursierDotFile.isDirectory())
                path = System.getProperty("user.home") + "/.coursier/cache/v1/";
        }

        if (path == null) {
            path = xdgPath;
            xdgDir.mkdirs();
        }

        return path;
    }

    public static File cacheDirectory() throws IOException {

        if (cacheDirectory0 == null)
            synchronized (cacheDirectoryLock) {
                if (cacheDirectory0 == null) {
                    cacheDirectory0 = new File(computeCacheDirectory()).getAbsoluteFile();
                }
            }

        return cacheDirectory0;
    }

    private static ProjectDirectories coursierDirectories() throws IOException {

        if (coursierDirectories0 == null)
            synchronized (coursierDirectoriesLock) {
                if (coursierDirectories0 == null) {
                    coursierDirectories0 = ProjectDirectories.from(null, null, "Coursier");
                }
            }

        return coursierDirectories0;
    }

    private static String computeConfigDirectory() throws IOException {
        String path = System.getenv("COURSIER_CONFIG_DIR");

        if (path == null)
            path = System.getProperty("coursier.config-dir");

        if (path != null)
          return path;

        return coursierDirectories().configDir;
    }

    public static File configDirectory() throws IOException {

        if (configDirectory0 == null)
            synchronized (configDirectoryLock) {
                if (configDirectory0 == null) {
                    configDirectory0 = new File(computeConfigDirectory()).getAbsoluteFile();
                }
            }

        return configDirectory0;
    }

    private static String computeDataLocalDirectory() throws IOException {
        String path = System.getenv("COURSIER_DATA_DIR");

        if (path == null)
            path = System.getProperty("coursier.data-dir");

        if (path != null)
          return path;

        return coursierDirectories().dataLocalDir;
    }

    public static File dataLocalDirectory() throws IOException {

        if (dataLocalDirectory0 == null)
            synchronized (dataLocalDirectoryLock) {
                if (dataLocalDirectory0 == null) {
                    dataLocalDirectory0 = new File(computeDataLocalDirectory()).getAbsoluteFile();
                }
            }

        return dataLocalDirectory0;
    }
}