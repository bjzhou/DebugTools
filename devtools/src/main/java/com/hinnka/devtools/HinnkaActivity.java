package com.hinnka.devtools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@SuppressLint("Registered")
public abstract class HinnkaActivity extends AppCompatActivity {

    private static int sActivityCount = 0;

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Point mScreenPoint = new Point();
    private boolean mDebugLifeCycle;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private String mLogTag = getClass().getSimpleName();
    private final int mActivityIndex = ++sActivityCount;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onCreate");
        }
        if (getIntent() != null) {
            onHandleIntent(getIntent());
        }
        int layoutResId = getLayoutResId();
        if (layoutResId != 0) {
            mView = getLayoutInflater().inflate(layoutResId, null);
            mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            setContentView(mView);
            onViewLoad();
            View root = findViewById(android.R.id.content);
            root.post(this::onViewVisible);
        }
    }

    protected abstract @LayoutRes int getLayoutResId();
    protected void onHandleIntent(@NonNull Intent intent) {}
    protected abstract void onViewLoad();
    protected void onViewVisible() {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onRequestPermissionsResult");
        }
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            int result = grantResults[i];
            String permission = permissions[i];
            if (result == PackageManager.PERMISSION_GRANTED) {
                granted.add(permission);
            } else if (result == PackageManager.PERMISSION_DENIED) {
                boolean shouldRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (shouldRationale) {
                    onShowPermissionRationale(permission);
                }
                denied.add(permission);
            }
        }
        onPermissionsGranted(granted);
        onPermissionsDenied(denied);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onActivityResult");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onNewIntent");
        }
        onHandleIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onPause");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onDestroy");
        }
        mDisposables.clear();
        if (this instanceof MessageCenter.MessageObserver) {
            MessageCenter.get().removeObserver((MessageCenter.MessageObserver) this);
        }
    }

    protected void onShowPermissionRationale(@NonNull String permission) {
    }

    protected void onPermissionsGranted(@NonNull List<String> permissions) {
    }

    protected void onPermissionsDenied(@NonNull List<String> permissions) {
    }

    public void delay(Runnable runnable, long delayMillis) {
        View root = findViewById(android.R.id.content);
        if (root != null) {
            root.postDelayed(runnable, delayMillis);
        } else {
            getHandler().postDelayed(runnable, delayMillis);
        }
    }

    public <T extends ViewDataBinding> T getBinding(Class<T> bindingClass) {
        T binding = DataBindingUtil.getBinding(mView);
        if (binding == null) {
            binding = DataBindingUtil.bind(mView);
        }
        return binding;
    }

    public void send(Object object) {
        MessageCenter.get().send(object);
    }

    public Handler getHandler() {
        return mMainHandler;
    }

    public void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, mActivityIndex);
    }

    public void addDisposable(Disposable disposable) {
        mDisposables.add(disposable);
    }

    public void setLogTag(String logTag) {
        mLogTag = logTag;
    }

    public void setDebugLifeCycle(boolean debug) {
        mDebugLifeCycle = debug;
    }

    public final int dp(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public final int sp(int sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public final int px2dp(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public final int px2sp(int px) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scale + 0.5f);
    }

    public final Point screenSize() {
        if (mScreenPoint.x == 0 || mScreenPoint.y == 0) {
            getWindowManager().getDefaultDisplay().getSize(mScreenPoint);
        }
        return mScreenPoint;
    }

    public int statusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        startActivity(newIntent(activityClass));
    }

    public Intent newIntent(Class<? extends Activity> activityClass) {
        return new Intent(this, activityClass);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindowManager().getDefaultDisplay().getSize(mScreenPoint);
    }

    public void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }
}
