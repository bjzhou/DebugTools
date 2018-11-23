package com.hinnka.devtools.ui.sp;

import android.os.Bundle;
import android.preference.Preference;

import com.hinnka.devtools.ui.DebugToolsActivity;
import com.hinnka.devtools.ui.base.BasePreferenceFragment;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class SPFragment extends BasePreferenceFragment implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
        SharedPrefUtil.sPrefDir = "/data/data/" + getActivity().getPackageName() + "/shared_prefs";

        for (String name : SharedPrefUtil.listAll()) {
            Preference p = new Preference(getActivity());
            p.setTitle(name);
            p.setOnPreferenceClickListener(this);
            getPreferenceScreen().addPreference(p);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        DebugToolsActivity activity = (DebugToolsActivity) getActivity();
        SPDetailFragment fragment = new SPDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", preference.getTitle().toString());
        fragment.setArguments(bundle);
        activity.startPreferenceFragment(fragment, true);
        return false;
    }
}
