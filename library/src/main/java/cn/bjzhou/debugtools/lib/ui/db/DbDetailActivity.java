package cn.bjzhou.debugtools.lib.ui.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bjzhou.debugtools.lib.R;

/**
 * author: zhoubinjia
 * date: 2017/1/3
 */
public class DbDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mColumnsValue;
    private EditText mExecEditText;
    private Button mExecButton;
    private TextView mResultTextView;
    private String dbName;
    private String tableName;
    private SQLiteDatabase db;
    private String[] mColumnNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_detail);
        findViews();

        dbName = getIntent().getStringExtra("db");
        tableName = getIntent().getStringExtra("table");
        db = SQLiteDatabase.openDatabase(getDatabasePath(dbName).getPath(),null, 0);
        setTitle(tableName);
        initColumnValue();
        mExecEditText.setText("select * from " + tableName);
        mExecButton.setOnClickListener(this);
        mExecButton.post(new Runnable() {
            @Override
            public void run() {
                mExecButton.performClick();
            }
        });
    }

    private void initColumnValue() {
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        mColumnNames = cursor.getColumnNames();
        mColumnsValue.setText(Arrays.toString(mColumnNames));
        cursor.close();
    }

    private void findViews() {
        mColumnsValue = (TextView) findViewById(R.id.columnsValue);
        mExecEditText = (EditText) findViewById(R.id.execEditText);
        mExecButton = (Button) findViewById(R.id.execButton);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        String exec = mExecEditText.getText().toString();
        Cursor c = db.rawQuery(exec, null);
        StringBuilder builder = new StringBuilder();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                List<String> values = new ArrayList<>();
                for (String column : mColumnNames) {
                    int index = c.getColumnIndex(column);
                    int type = c.getType(index);
                    String value = "null";
                    switch (type) {
                        case Cursor.FIELD_TYPE_NULL:
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            value = String.valueOf(c.getInt(index));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            value = String.valueOf(c.getFloat(index));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            value = c.getString(index);
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            value = new String(c.getBlob(index));
                            break;
                    }
                    values.add(value);
                }
                builder.append(values.toString()).append("\n");
                c.moveToNext();
            }
        }
        mResultTextView.setText(builder.toString());
        c.close();
    }
}
