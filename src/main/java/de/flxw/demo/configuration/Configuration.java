package de.flxw.demo.configuration;

public class Configuration {
    private static final String[] SUPPORTED_EXTENSIONS = {"png", "jpg", "jpeg"};
    private static final String APP_DIR = "graphics-viewer";

    public static String getAppDir() {
        return APP_DIR;
    }

    public static String[] getSupportedFileExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
}
