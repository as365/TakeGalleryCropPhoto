package com.ediantong.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ediantong.App;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 对SharedPreference文件中的各种类型的数据进行存取操作
 */
public class SPUtils {

    private static SharedPreferences sp;

    private static void init() {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        }
    }

    public static boolean putInt( String key, int value) {
        if (sp == null) {
            init();
        }
        return sp.edit().putInt(key, value).commit();
    }

    public static int getInt( String key, int defaultValue) {
        if (sp == null) {
            init();
        }
        return sp.getInt(key, defaultValue);
    }

    public static boolean putlong( String key, long value) {
        if (sp == null) {
            init();
        }
        return sp.edit().putLong(key, value).commit();
    }

    public static long getlong( String key, long defaultValue) {
        if (sp == null) {
            init();
        }
        return sp.getLong(key, defaultValue);
    }

    public static boolean putFloat( String key,
                                   float value) {
        if (sp == null) {
            init();
        }
        return sp.edit().putFloat(key, value).commit();
    }

    public static Float getFloat( String key, float defaultValue) {
        if (sp == null) {
            init();
        }
        return sp.getFloat(key, defaultValue);
    }

    public static boolean putBoolean( String key,
                                     boolean value) {
        if (sp == null) {
            init();
        }
        return sp.edit().putBoolean(key, value).commit();
    }

    public static Boolean getBoolean( String key, boolean defaultValue) {
        if (sp == null) {
            init();
        }
        return sp.getBoolean(key, defaultValue);
    }

    public static boolean putString( String key, String value) {
        if (sp == null) {
            init();
        }
        return sp.edit().putString(key, value).commit();
    }

    public static String getString( String key, String defaultValue) {
        if (sp == null) {
            init();
        }
        return sp.getString(key, defaultValue);
    }

    public static boolean putStringSet(String key, Set<String> set) {

        return sp.edit().putStringSet(key, set).commit();
    }

    public static Set<String> getStringSet(String key) {

        return sp.getStringSet(key, new LinkedHashSet<String>());
    }

    public static boolean remove(String key) {
        return sp.edit().remove(key).commit();
    }

}
