package com.github.programmerr47.imageviewer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.github.programmerr47.imageviewer.imageloading.cache.MemoryCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michael Spitsin
 * @since 2015-08-07
 */
public class BitmapUtils {

    public static Bitmap getBitmapFromInternet(String url, File file, MemoryCache memoryCache) {
        try {
            URL imageUrl = getUrl(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(file);
            copyStream(is, os);
            os.close();
            return decodeFile(file);
        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    private static URL getUrl(String url) throws MalformedURLException {
        url = fixUrl(url);
        return new URL(url);
    }

    private static String fixUrl(String url) {
        if (url.startsWith("http://")) {
            return url;
        } else if (url.startsWith("//")) {
            return "http:" + url;
        } else {
            return "http://" + url;
        }
    }

    public static Bitmap decodeFile(File file) {
        if (!file.isDirectory()) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            try {
                return BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            } catch (FileNotFoundException e) {
//                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private static void copyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Return the size of a directory in bytes
     */
    public static int dirSize(File dir) {

        if (dir.exists()) {
            int result = 0;
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    result += dirSize(file);
                } else {
                    result += file.length();
                }
            }
            return result;
        }
        return 0;
    }
}
