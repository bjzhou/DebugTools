package com.hinnka.devtools.ui.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class SharedPrefUtil {
    public static String sPrefDir = "";

    public static List<String> listAll() {
        if (TextUtils.isEmpty(sPrefDir)) return Collections.emptyList();
        File file = new File(sPrefDir);
        if (file.exists() && file.isDirectory()) {
            List<String> names = new ArrayList<>();
            for (String path : file.list()) {
                if (path.endsWith(".xml")) {
                    names.add(path.substring(0, path.length() - 4));
                }
            }
            return names;
        }
        return Collections.emptyList();
    }

    public static SharedPreferences get(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static Map<String, ?> getAllValue(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).getAll();
    }

    public static void putValue(Context context, String name, String key, Object value) {
        if (value == null) return;
        SharedPreferences.Editor editor = get(context, name).edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            Set<String> newSet = new HashSet<>();
            for (Object o : (Set) value) {
                if (o instanceof String) {
                    newSet.add((String) o);
                }
            }
            editor.putStringSet(key, newSet);
        } else {
            throw new UnsupportedOperationException();
        }
        editor.apply();
    }
}
