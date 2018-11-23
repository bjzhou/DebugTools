package com.hinnka.devtools.ui.file;

import java.util.Locale;

/**
 * author: zhoubinjia
 * date: 2017/1/4
 */
public class FileUtil {

    public static String getDisplaySize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize < 1024 * 1024) {
            return String.format(Locale.US, "%.2f", fileSize * 1.0f / 1024) + "KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format(Locale.US, "%.2f", fileSize * 1.0f / 1024 / 1024) + "MB";
        } else {
            return String.format(Locale.US, "%.2f", fileSize * 1.0f / 1024 / 1024 / 1024) + "GB";
        }
    }
}
