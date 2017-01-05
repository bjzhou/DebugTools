package cn.bjzhou.debugtools.lib.ui.db;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;

import java.util.ArrayList;
import java.util.List;

import cn.bjzhou.debugtools.lib.ui.base.BasePreferenceFragment;

/**
 * author: zhoubinjia
 * date: 2017/1/3
 */
public class DbTableFragment extends BasePreferenceFragment implements Preference.OnPreferenceClickListener {
    private String dbName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
        Bundle bundle = getArguments();
        dbName = bundle.getString("db");
        List<String> tables = getTables();
        for (String table : tables) {
            Preference p = new Preference(getActivity());
            p.setTitle(table);
            p.setOnPreferenceClickListener(this);
            getPreferenceScreen().addPreference(p);
        }
    }

    private List<String> getTables() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(getActivity().getDatabasePath(dbName).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        ArrayList<String> arrTblNames = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex("name"));
                if (!name.equals("android_metadata") && !name.equals("sqlite_sequence")) {
                    arrTblNames.add(name);
                }
                c.moveToNext();
            }
        }
        c.close();
        database.close();
        return arrTblNames;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(getActivity(), DbDetailActivity.class);
        intent.putExtra("db", dbName);
        intent.putExtra("table", preference.getTitle());
        startActivity(intent);
        return false;
    }
}
