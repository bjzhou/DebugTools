package cn.bjzhou.debugtools.lib.ui.anr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.text.TextUtils;

import java.util.List;

import cn.bjzhou.debugtools.lib.ui.base.BasePreferenceFragment;
import cn.bjzhou.debugtools.lib.ui.log.LogDetailActivity;

/**
 * author: zhoubinjia
 * date: 2017/1/4
 */
public class ANRFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
        final ANRTask task = new ANRTask();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ANR Messages...");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                task.cancel(true);
                getActivity().onBackPressed();
            }
        });
        progressDialog.show();
        task.load(new ANRTask.OnLoadListener() {
            @Override
            public void onLoad(List<ANR> anrs) {
                if (getActivity() == null) return;
                for (final ANR anr : anrs) {
                    String title = anr.getPackageName();
                    if (TextUtils.isEmpty(title)) {
                        title = anr.getPid();
                    }
                    Preference p = new Preference(getActivity());
                    p.setTitle(title + "(" + anr.getDate() + " " + anr.getTime() + ")");
                    p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = new Intent(getActivity(), LogDetailActivity.class);
                            intent.putExtra("anr", anr);
                            startActivity(intent);
                            return false;
                        }
                    });
                    getPreferenceScreen().addPreference(p);
                }
                progressDialog.dismiss();
            }
        });
    }
}
