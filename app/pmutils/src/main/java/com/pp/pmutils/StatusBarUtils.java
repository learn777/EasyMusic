package com.pp.pmutils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author PPM
 * @Time 2020/8/16 12:32
 * @className StatusBarUtils
 * @description *****
 */
public final class StatusBarUtils {
    /**
     * @param activity one of the activity to translucent status_bar
     * @param flag     When set to true,status bar will translucent, otherwise not translucent
     */
    public static void setBarTranslucent(Activity activity, boolean flag) {
        if (flag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.0 全透明实现
                //getWindow.setStatusBarColor(Color.TRANSPARENT)
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //4.4 全透明状态栏
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * @param activity one of the activity to translucent status_bar
     * @param isDark   When set to true,status bar font will become white,otherwise font color is default
     */
    public static void setStatusBarFontColor(Activity activity, boolean isDark) {
        if (isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
