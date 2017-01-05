package cn.bjzhou.debugtools.lib.ui.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class LogTask {
    private volatile boolean alive = false;
    private StringBuffer logSb = new StringBuffer();
    private String tag = "*";
    private String level = "V";
    private String filter;
    private OnUpdateListener listener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Handler threadHandler;
    private Process logcatProcess;
    private String buffer;

    LogTask() {
        HandlerThread thread = new HandlerThread("LogTask");
        thread.start();
        threadHandler = new Handler(thread.getLooper());
    }

    private void logcat() {
        try {
            List<String> commands = new ArrayList<>();
            Collections.addAll(commands, "logcat", "-v", "time", "-s", tag + ":" + level);
            if (!TextUtils.isEmpty(buffer)) {
                commands.add(1, "-b");
                commands.add(2, buffer);
            }
            logcatProcess = new ProcessBuilder()
                    .command(commands)
                    .redirectErrorStream(true)
                    .start();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            while (alive && (line = reader.readLine()) != null) {
                if (!TextUtils.isEmpty(filter) && !line.contains(filter)) continue;
                logSb.append(line).append("\n");
                if (listener != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onUpdate(logSb.toString());
                        }
                    });
                    SystemClock.sleep(32);
                }
            }
            reader.close();
        } catch (IOException e) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
            logSb.append(os.toString());
        }
    }

    public void setTag(String tag) {
        if (TextUtils.isEmpty(tag)) return;
        this.tag = tag;
    }

    public void setLevel(String level) {
        if (TextUtils.isEmpty(level)) return;
        this.level = level;
    }

    public synchronized void clear() {
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }
        alive = false;
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec("logcat -c").waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logSb = new StringBuffer();
                alive = true;
                logcat();
            }
        });
    }

    public void setListener(OnUpdateListener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                alive = true;
                logcat();
            }
        });
    }

    public synchronized void release() {
        alive = false;
        if (logcatProcess != null) {
            logcatProcess.destroy();
        }
    }

    public String getCurrentLog() {
        return logSb.toString();
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    interface OnUpdateListener {
        void onUpdate(String log);
    }
}
