package cn.it.com.theroy.uitls;

import android.content.Context;
import android.content.SharedPreferences;

import cn.it.com.theroy.iview.TheroyApplication;


public class PreferencesUtils {

    private final static String PREFERENCES_CONFIG = "preferences_config";

    public interface SPKeys {
        String CHAPTER_TAG = "chapter_tag";
        String CHAPTER_INDEX = "chapter_index";
        String DETAIL_TXT_PATH = "detail_txt_path";
        String DETAIL_TXT_TITLE = "detail_txt_title";
        String DETAIL_AUDIO_PATH = "detail_audio_path";
        String DETAIL_POSITION = "detail_position";
    }

    private static SharedPreferences getPreferences() {
        return TheroyApplication.getContext().getSharedPreferences(PREFERENCES_CONFIG, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        getPreferences().edit().putString(key, value).commit();
    }

    public static String getString(String key, String defaultValue) {
        return getPreferences().getString(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        getPreferences().edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }
}
