package com.wyc.dialogsimple.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;


public class ScreenUtils {
    private static final String TAG = "ScreenUtils";
    private static int sScreenWidth = 0;
    private static int sScreenHeight = 0;

    public static int getScreenWidth() {
        if (sScreenWidth == 0) {
            init();
        }
        return sScreenWidth;
    }

    public static int getScreenHeight() {
        if (sScreenHeight == 0) {
            init();
        }
        return sScreenHeight;
    }


    private static void init() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        sScreenWidth = metrics.widthPixels;
        sScreenHeight = metrics.heightPixels;
        if (sScreenWidth > sScreenHeight) {
            int temp = sScreenWidth;
            sScreenWidth = sScreenHeight;
            sScreenHeight = temp;
        }
    }

    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
