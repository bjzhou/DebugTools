package cn.bjzhou.debugtools.lib.ui.log;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import cn.bjzhou.debugtools.lib.R;
import cn.bjzhou.debugtools.lib.ui.base.BasePreferenceFragment;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class LogFragment extends BasePreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_log);
        findPreference("log_all").setOnPreferenceClickListener(this);
        findPreference("log_crash").setOnPreferenceClickListener(this);
        findPreference("log_tag_text").setOnPreferenceChangeListener(this);
        findPreference("log_filter").setOnPreferenceChangeListener(this);
        findPreference("log_level_list").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Intent intent = new Intent(getActivity(), LogDetailActivity.class);
        switch (preference.getKey()) {
            case "log_tag_text":
                intent.putExtra("tag", (String) newValue);
                break;
            case "log_level_list":
                intent.putExtra("level", (String) newValue);
                break;
            case "log_filter":
                intent.putExtra("filter", (String) newValue);
                break;
        }
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(getActivity(), LogDetailActivity.class);
        switch (preference.getKey()) {
            case "log_all":
                break;
            case "log_crash":
                intent.putExtra("tag", "AndroidRuntime");
                break;
        }
        startActivity(intent);
        return false;
    }
}
