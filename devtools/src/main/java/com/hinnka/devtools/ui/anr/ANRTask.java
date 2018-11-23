package com.hinnka.devtools.ui.anr;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * author: zhoubinjia
 * date: 2017/1/4
 */
class ANRTask extends AsyncTask<Void, Void, List<ANR>> {

    private OnLoadListener listener;

    private List<ANR> anr() {
        List<ANR> anrs = new ArrayList<>();
        try {
            File file = new File("/data/anr/traces.txt");
            if (!file.exists() || file.isDirectory()) return anrs;
            ANR anr = new ANR();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("----- pid")) {
                    String[] splits = line.split(" ");
                    anr.setPid(splits[2]);
                    anr.setDate(splits[4]);
                    anr.setTime(splits[5]);
                } else if (line.startsWith("----- end")) {
                    String[] splits = line.split(" ");
                    if (TextUtils.isEmpty(anr.getPid())) {
                        anr.setPid(splits[2]);
                    }
                    anrs.add(anr);
                    anr = new ANR();
                } else if (line.startsWith("Cmd line")) {
                    String pkg = line.split(": ")[1];
                    anr.setPackageName(pkg);
                } else {
                    anr.setLog(anr.getLog() + line + "\n");
                }
            }
            reader.close();
        } catch (IOException e) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            e.printStackTrace(ps);
        }
        return anrs;
    }

    @Override
    protected List<ANR> doInBackground(Void... params) {
        return anr();
    }

    @Override
    protected void onPostExecute(List<ANR> anrs) {
        listener.onLoad(anrs);
    }

    void load(OnLoadListener listener) {
        this.listener = listener;
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    interface OnLoadListener {
        void onLoad(List<ANR> anrs);
    }
}
