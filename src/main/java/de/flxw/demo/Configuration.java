package de.flxw.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

public class Configuration {
    private static final String DB_NAME = "imagedb.java.serialized";
    private static final String[] SUPPORTED_EXTENSIONS = {"png", "jpg", "jpeg"};
    private static final String APP_DIR = "graphics-viewer";
    private static Logger l = LoggerFactory.getLogger(Configuration.class);

    public static String getDbLocation() {
        return Paths.get(APP_DIR, DB_NAME).toString();
    }

    public static String getAppDir() {
        return APP_DIR;
    }

    public static String[] getSupportedFileExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    public static boolean createAppDirIfNonExistent() {
        File dir = new File(APP_DIR);
        boolean returnValue = false;

        if (dir.exists()) {
            for(File file: Objects.requireNonNull(dir.listFiles())) {
                if (!file.isDirectory()) file.delete();
            }

            returnValue = false;
        } else {
            try {
                dir.mkdir();
                returnValue = true;
            } catch (SecurityException se) {
                l.error("Could not create application directory!");
                l.error(dir.getAbsolutePath());
                l.error("Exiting because this prevents all further operations. Please check permissions.");
            }
        }

        return returnValue;
    }
}
