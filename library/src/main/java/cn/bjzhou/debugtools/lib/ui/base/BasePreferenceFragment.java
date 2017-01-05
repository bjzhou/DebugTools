package cn.bjzhou.debugtools.lib.ui.base;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class BasePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
