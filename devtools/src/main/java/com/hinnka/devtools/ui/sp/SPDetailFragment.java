package com.hinnka.devtools.ui.sp;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;

import com.hinnka.devtools.ui.base.BasePreferenceFragment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * author: zhoubinjia
 * date: 2017/1/3
 */
public class SPDetailFragment extends BasePreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));

        Bundle bundle = getArguments();
        name = bundle.getString("key");
        for (Map.Entry<String, ?> entry : SharedPrefUtil.getAllValue(getActivity(), name).entrySet()) {
            EditTextPreference preference = new EditTextPreference(getActivity());
            preference.setTitle(entry.getKey());
            preference.setSummary(String.valueOf(entry.getValue()));
            preference.setDefaultValue(String.valueOf(entry.getValue()));
            preference.setOnPreferenceChangeListener(this);
            getPreferenceScreen().addPreference(preference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String) newValue;
        Object realValue;
        try {
            realValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            try {
                realValue = Float.parseFloat(value);
            } catch (NumberFormatException e1) {
                try {
                    realValue = Long.parseLong(value);
                } catch (NumberFormatException e2) {
                    if (value.equals("true") || value.equals("false")) {
                        realValue = Boolean.parseBoolean(value);
                    } else if (value.startsWith("[") && value.endsWith("]")) {
                        Set<String> set = new HashSet<>();
                        String[] strs = value.substring(1, value.length() - 1).split(", ");
                        Collections.addAll(set, strs);
                        realValue = set;
                    } else {
                        realValue = value;
                    }
                }
            }
        }
        SharedPrefUtil.putValue(getActivity(), name, preference.getTitle().toString(), realValue);
        preference.setSummary(value);
        return false;
    }
}
