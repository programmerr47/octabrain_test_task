package com.github.programmerr47.imageviewer.util;

/**
 * All constants that important and common for representation part (activities, fragments, adapters, views etc)
 *
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class Constants {

    public enum Font {
        ROBOTO_BOLD("Roboto-Bold.ttf"),
        ROBOTO_MEDIUM("Roboto-Medium.ttf"),
        ROBOTO_REGULAR("Roboto-Regular.ttf");

        private String fontName;

        Font(String fontName) {
            this.fontName = fontName;
        }

        public String getFontName() {
            return fontName;
        }
    }

    private Constants() {}

    /**
     * Directory where all custom fonts are stored.
     */
    public static final String ASSETS_FONTS_DIR = "fonts/";

    public static final int TOOLBAR_ELEVATION_DEFAULT = 4;

    public static final int DEFAULT_ANIMATION_DURATION = 300;
}
