package cn.bjzhou.debugtools.lib.ui.log;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import cn.bjzhou.debugtools.lib.R;
import cn.bjzhou.debugtools.lib.ui.anr.ANR;

/**
 * author: zhoubinjia
 * date: 2017/1/2
 */
public class LogDetailActivity extends AppCompatActivity implements LogTask.OnUpdateListener, SearchView.OnQueryTextListener {
    private LogTask mLogTask;

    private TextView mLogText;
    private boolean mSearchMode;
    private String mFilterText;
    private String mAllLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tag = getIntent().getStringExtra("tag");
        String level = getIntent().getStringExtra("level");
        String filter = getIntent().getStringExtra("filter");
        String buffer = getIntent().getStringExtra("buffer");
        ANR anr = getIntent().getParcelableExtra("anr");
        setContentView(R.layout.activity_log_detail);
        findViews();
        ActionBar actionBar = getSupportActionBar();
        mLogText.setText("Waiting...");
        String title = "Log Detail";
        if (anr != null) {
            title = anr.getPackageName();
            if (TextUtils.isEmpty(title)) {
                title = anr.getPid();
            }
            setTitle(title);
            mAllLog = anr.getLog();
            mLogText.setText(anr.getLog());
        } else {
            mLogTask = new LogTask();
            mLogTask.setTag(tag);
            mLogTask.setLevel(level);
            mLogTask.setFilter(filter);
            mLogTask.setBuffer(buffer);
            mLogTask.setListener(this);
            mLogTask.start();
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    private void findViews() {
        mLogText = (TextView) findViewById(R.id.logText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchMode = false;
                mLogText.setText(mAllLog);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.clear) {
            mLogText.setText("");
            mLogTask.clear();
            return true;
        } else if (i == R.id.app_bar_search) {
            return true;
        } else if (i == R.id.copy) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("log", mLogText.getText()));
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLogTask != null) {
            mLogTask.release();
        }
    }

    @Override
    public void onUpdate(String log) {
        mAllLog = log;
        filter(mAllLog, mFilterText);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchMode = true;
        mFilterText = newText;
        filter(mAllLog, mFilterText);
        return true;
    }

    private void filter(String all, String str) {
        if (!mSearchMode || TextUtils.isEmpty(str)) {
            mLogText.setText(all);
            return;
        }
        StringBuilder builder = new StringBuilder();
        String[] lines = all.split("\n");
        for (String line : lines) {
            if (line.contains(str)) {
                builder.append(line).append("\n");
            }
        }
        mLogText.setText(builder.toString());
    }
}
