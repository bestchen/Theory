package cn.it.com.theroy.uitls;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by Chenweiwei on 2017/7/29.
 */

public class UIUitls {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }

    public static int getPixels(float dip) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getDisplayMetrics()));
    }


    public static DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    public static Resources getResources() {
        return sContext.getResources();
    }

    /**
     * 获取屏幕分辨率宽度 *
     */
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕分辨率高度 *
     */
    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static String getString(int resource) {
        return sContext.getString(resource);
    }

    public static String getString(int resource, Object... formatArgs) {
        return sContext.getString(resource, formatArgs);
    }

    public static int getResourceId(String name) {
        return getResources().getIdentifier(name, null, sContext.getPackageName());
    }

    public static Drawable getDrawable(int resource) {
        return getResources().getDrawable(resource);
    }

    public static int getColor(int resource) {
        return getResources().getColor(resource);
    }

    public static Bitmap getBitmap(int resource) {
        return BitmapFactory.decodeResource(getResources(), resource);
    }

    /**
     * 像素转换成DIP *
     */
    public static int getDips(float pixel) {
        if (pixel >= 0) {
            return (int) (pixel / getDisplayMetrics().density + 0.5f);
        } else {
            return 0;
        }
    }

    public static int getDimensionPixelSize(int id) {
        return getResources().getDimensionPixelSize(id);
    }

    public static int sp2pix(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDisplayMetrics()));
    }

    /**
     * 解析图片文件，返回图片对象。根据设备自动调整图片大小
     *
     * @param file
     * @return
     */
    public static Bitmap decodeResourceBitmap(File file, int resid) {
        if (!file.exists()) {
            return null;
        }
        try {
            return decodeResourceBitmap(new FileInputStream(file), resid);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static Bitmap decodeResourceBitmap(InputStream is, int resId) {
        try {
            Rect pad = new Rect();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScreenDensity = getDisplayMetrics().densityDpi;
            TypedValue value = new TypedValue();
            Resources resources = getResources();
            resources.getValue(resId, value, false);

            final int density = value.density;
            if (density == TypedValue.DENSITY_DEFAULT) {
                opts.inDensity = DisplayMetrics.DENSITY_DEFAULT;
            } else if (density != TypedValue.DENSITY_NONE) {
                opts.inDensity = density;
            }
            opts.inTargetDensity = resources.getDisplayMetrics().densityDpi;

            Bitmap bitmap = BitmapFactory.decodeStream(is, pad, opts);
            is.close();
            return bitmap;
        } catch (Throwable e) {
        }

        return null;
    }


    public static InputMethodManager getInputMethodManager() {
        return (InputMethodManager) sContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static void hideInputMethod(Activity activity) {
        InputMethodManager im = ((InputMethodManager) sContext.getSystemService(Activity.INPUT_METHOD_SERVICE));
        View curFocusView = activity.getCurrentFocus();
        if (curFocusView != null) {
            im.hideSoftInputFromWindow(curFocusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static RectF getViewLocationOnScreen(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int vx = location[0];
        int vy = location[1];
        RectF viewRectF = new RectF(vx, vy, vx + v.getMeasuredWidth(), vy + v.getMeasuredHeight());
        return viewRectF;
    }

    public static void switchFullscreen(Activity activity, boolean isFullscreen) {
        if (isFullscreen) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
        }
    }

    /**
     * 获取真正屏幕高度（若有虚拟按键，会加上虚拟按键）
     *
     * @return
     */
    public static int getRealScreenHeight() {
        int h = 0;
        WindowManager windowManager = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            h = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (h <= 0)
            h = getScreenHeight();
        return h;
    }

    public static LinearLayout.LayoutParams generateLayoputParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.leftMargin = UIUitls.getPixels(10);
        params.rightMargin = UIUitls.getPixels(10);
        return params;
    }
}
