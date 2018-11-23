package com.hinnka.devtools.ui.db;

import android.os.Bundle;
import android.preference.Preference;

import com.hinnka.devtools.ui.DebugToolsActivity;
import com.hinnka.devtools.ui.base.BasePreferenceFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class DatabaseFragment extends BasePreferenceFragment implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
        List<String> databases = getDatabases();
        for (String db : databases) {
            Preference p = new Preference(getActivity());
            p.setTitle(db);
            p.setOnPreferenceClickListener(this);
            getPreferenceScreen().addPreference(p);
        }
    }

    private List<String> getDatabases() {
        List<String> databases = new ArrayList<>();
        File file = new File("/data/data/" + getActivity().getPackageName() + "/databases");
        if (file.exists() && file.isDirectory()) {
            for (String path : file.list()) {
                if (!path.endsWith("-journal")) {
                    databases.add(path);
                }
            }
        }
        return databases;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        DebugToolsActivity activity = (DebugToolsActivity) getActivity();
        DbTableFragment fragment = new DbTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString("db", preference.getTitle().toString());
        fragment.setArguments(bundle);
        activity.startPreferenceFragment(fragment, true);
        return false;
    }
}
