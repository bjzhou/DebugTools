package cn.bjzhou.debugtools.lib.ui.file;

import android.os.Bundle;
import android.preference.Preference;
import android.text.TextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import cn.bjzhou.debugtools.lib.ui.DebugToolsActivity;
import cn.bjzhou.debugtools.lib.ui.base.BasePreferenceFragment;

/**
 * author: zhoubinjia
 * date: 2017/1/4
 */
public class FileFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
        File dir = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            String path = bundle.getString("file");
            if (!TextUtils.isEmpty(path)) {
                dir = new File(path);
            }
        }
        if (dir == null) {
            dir = new File("/data/data/" + getActivity().getPackageName());
        }
        if (dir.exists() && dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            Arrays.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && !o2.isDirectory()) return -1;
                    if (!o1.isDirectory() && o2.isDirectory()) return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (final File file : fileList) {
                Preference p = new Preference(getActivity());
                p.setTitle(file.getName());
                if (file.isFile()) {
                    p.setSummary(FileUtil.getDisplaySize(file.length()));
                } else {
                    p.setSummary("Directory");
                }
                p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (file.isDirectory()) {
                            DebugToolsActivity activity = (DebugToolsActivity) getActivity();
                            FileFragment fragment = new FileFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("file", file.getPath());
                            fragment.setArguments(bundle);
                            activity.startPreferenceFragment(fragment, true);
                        }
                        return false;
                    }
                });
                getPreferenceScreen().addPreference(p);
            }
        }
    }
}
