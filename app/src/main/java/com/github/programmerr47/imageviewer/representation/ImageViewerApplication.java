package com.github.programmerr47.imageviewer.representation;

import android.app.Application;
import android.content.Context;

import com.github.programmerr47.imageviewer.imageloading.ImageLoader;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class ImageViewerApplication extends Application {

    private static Context context;
    private static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        imageLoader = new ImageLoader(context);
    }

    public static Context getAppContext() {
        return context;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }
}
