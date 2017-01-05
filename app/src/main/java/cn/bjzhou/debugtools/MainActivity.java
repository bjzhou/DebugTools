package cn.bjzhou.debugtools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashSet;
import java.util.Set;

import cn.bjzhou.debugtools.lib.DebugTools;
import cn.bjzhou.debugtools.lib.ui.sp.SharedPrefUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout mActivityMain;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DebugTools.init(this);

        findViews();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefUtil.putValue(MainActivity.this, "testA", "testBoolean", true);
                SharedPrefUtil.putValue(MainActivity.this, "testB", "testInt", 1);
                SharedPrefUtil.putValue(MainActivity.this, "testC", "testFloat", 1.0f);
                SharedPrefUtil.putValue(MainActivity.this, "testD", "testString", "wtf");
                Set<String> set = new HashSet<>();
                set.add("A");
                set.add("B");
                set.add("1.0");
                set.add("true");
                SharedPrefUtil.putValue(MainActivity.this, "testE", "testStringSet", set);
                testLogs();
            }
        });
    }

    private void testLogs() {
        Log.d(TAG, "testLogs: ");
        mButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                testLogs();
            }
        }, 1000);
    }

    private void findViews() {
        mActivityMain = (LinearLayout) findViewById(R.id.activity_main);
        mButton = (Button) findViewById(R.id.button);
    }
}
