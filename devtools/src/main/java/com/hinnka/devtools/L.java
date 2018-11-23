package com.hinnka.devtools;

import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class L {

    public static boolean DEBUG = true;

    private static final String TAG_DEFAULT = "Hinnka";
    private static final int STACK_DEPTH = 5;

    public static void v(Object... args) {
        if (BuildConfig.DEBUG && args.length != 0) {
            logFully(TAG_DEFAULT, Arrays.deepToString(args), null, false, Log.VERBOSE);
        }
    }

    public static void i(Object... args) {
        if (args.length != 0) {
            logFully(TAG_DEFAULT, Arrays.deepToString(args), null, false, Log.INFO);
        }
    }

    public static void d(Object... args) {
        if (args.length != 0) {
            logFully(TAG_DEFAULT, Arrays.deepToString(args), null, true, Log.DEBUG);
        }
    }

    public static void w(Object... args) {
        if (args.length != 0) {
            logFully(TAG_DEFAULT, Arrays.deepToString(args), null, true, Log.WARN);
        }
    }

    public static void e(Throwable e) {
        logFully(TAG_DEFAULT, "", e, true, Log.ERROR);
    }

    public static void e(String msg, Throwable e) {
        logFully(TAG_DEFAULT, msg, e, true, Log.ERROR);
    }

    public static void e(String tag, String msg, Throwable e) {
        logFully(tag, msg, e, true, Log.ERROR);
    }

    /**
     * @param level The order in terms of verbosity, from least to most is
     *              ERROR, WARN, INFO, DEBUG, VERBOSE.  Verbose should never be compiled
     *              into an application except during development.  Debug logs are compiled
     *              in but stripped at runtime.  Error, warning and info logs are always kept.
     */
    private static void logFully(String tag, String msg, Throwable e, boolean withStackTrace, int level) {
        StringBuilder msgBuilder = new StringBuilder();
        if (withStackTrace) {
            msgBuilder.append(getStackTraceMsg(STACK_DEPTH)).append(": ");
        }
        if (!TextUtils.isEmpty(msg)) {
            msgBuilder.append(msg);
        }
        if (e != null) {
            if (!TextUtils.isEmpty(msg)) {
                msgBuilder.append("\n");
            }
            msgBuilder.append(printError(e));
        }
        String fullMsg = msgBuilder.toString();

        switch (level) {
            case Log.VERBOSE:
                if (DEBUG) Log.v(tag, fullMsg);
                break;
            case Log.DEBUG:
                if (DEBUG) Log.d(tag, fullMsg);
                break;
            case Log.INFO:
                Log.i(tag, fullMsg);
                break;
            case Log.WARN:
                Log.w(tag, fullMsg);
                break;
            case Log.ERROR:
                Log.e(tag, fullMsg);
                break;
        }
    }

    private static String getStackTraceMsg(int depth) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[depth];
        return stackTrace.getMethodName() +
                (stackTrace.isNativeMethod() ? "(Native Method)" :
                        (stackTrace.getFileName() != null && stackTrace.getLineNumber() >= 0 ?
                                "(" + stackTrace.getFileName() + ":" + stackTrace.getLineNumber() + ")" :
                                (stackTrace.getFileName() != null ? "(" + stackTrace.getFileName() + ")" : "(Unknown Source)")));
    }

    public static String printError(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}
