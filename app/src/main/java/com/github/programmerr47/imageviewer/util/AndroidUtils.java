package com.github.programmerr47.imageviewer.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.github.programmerr47.imageviewer.representation.ImageViewerApplication;

/**
 * Some helpful functions related to android os.
 *
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class AndroidUtils {

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static float dpToPx(@NonNull Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static float dpToPx(int dp) {
        return dpToPx(ImageViewerApplication.getAppContext(), dp);
    }

    public static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
               activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isNetworkConnected() {
        return isNetworkConnected(ImageViewerApplication.getAppContext());
    }

    public static int color(@ColorRes int colorRes) {
        return color(ImageViewerApplication.getAppContext(), colorRes);
    }

    public static int color(@NonNull Context context, @ColorRes int colorRes) {
        Resources r = context.getResources();
        return r.getColor(colorRes);
    }
}
