package com.hinnka.devtools;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class HinnkaFragment extends Fragment {

    private static int sFragmentCount = 0;

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Point mScreenPoint = new Point();
    private boolean mDebugLifeCycle;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private String mLogTag = getClass().getSimpleName();
    private int mFragmentCount = ++sFragmentCount;
    private boolean previousVisible;
    private View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onCreate");
        }
        if (getArguments() != null) {
            onHandleArguments(getArguments());
        }
    }

    @Nullable
    @Deprecated
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onCreateView");
        }
        int layoutResId = getLayoutResID();
        if (layoutResId != 0) {
            mView = inflater.inflate(layoutResId, container, false);
            return mView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public <T extends ViewDataBinding> T getBinding(Class<T> bindingClass) {
        T binding = DataBindingUtil.getBinding(mView);
        if (binding == null) {
            binding = DataBindingUtil.bind(mView);
        }
        return binding;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onAttach");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onAttach");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onRequestPermissionsResult");
        }
        Activity activity = getActivity();
        if (activity == null) return;
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            int result = grantResults[i];
            String permission = permissions[i];
            if (result == PackageManager.PERMISSION_GRANTED) {
                granted.add(permission);
            } else if (result == PackageManager.PERMISSION_DENIED) {
                boolean shouldRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onActivityResult");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onStart");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onResume");
        }
        if (getUserVisibleHint()) {
            onFragmentVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onPause");
        }
        if (getUserVisibleHint()) {
            onFragmentHidden();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mDebugLifeCycle) {
            L.d(mLogTag, "setUserVisibleHint");
        }
        if (!isAdded()) return;
        if (isResumed()) {
            if (isVisibleToUser) {
                onFragmentVisible();
            } else {
                onFragmentHidden();
            }
        }
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof HinnkaFragment) {
                if (isVisibleToUser) {
                    if (((HinnkaFragment) fragment).previousVisible) {
                        fragment.setUserVisibleHint(true);
                    }
                } else {
                    if (fragment.getUserVisibleHint()) {
                        ((HinnkaFragment) fragment).previousVisible = fragment.getUserVisibleHint();
                        fragment.setUserVisibleHint(false);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onDestroy");
        }
        mDisposables.clear();
        if (this instanceof MessageCenter.MessageObserver) {
            MessageCenter.get().removeObserver((MessageCenter.MessageObserver) this);
        }
    }

    public void send(Object message) {
        MessageCenter.get().send(message);
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

    protected void onFragmentVisible() {
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onFragmentVisible");
        }
        View view = getView();
        if (view != null) {
            view.requestFocus();
        }
    }

    protected void onFragmentHidden() {
        if (mDebugLifeCycle) {
            L.d(mLogTag, "onFragmentHidden");
        }
        View view = getView();
        if (view != null) {
            getView().clearFocus();
            hideKeyboard(view);
        }
    }

    protected void onHandleArguments(@NonNull Bundle bundle) {
    }

    protected void onShowPermissionRationale(@NonNull String permission) {
    }

    protected void onPermissionsGranted(@NonNull List<String> permissions) {
    }

    protected void onPermissionsDenied(@NonNull List<String> permissions) {
    }

    protected abstract @LayoutRes int getLayoutResID();

    public Handler getHandler() {
        return mMainHandler;
    }

    public void requestPermissions(String... permissions) {
        Activity activity = getActivity();
        if (activity == null) return;
        ActivityCompat.requestPermissions(activity, permissions, mFragmentCount);
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
            Activity activity = getActivity();
            if (activity == null) return mScreenPoint;
            activity.getWindowManager().getDefaultDisplay().getSize(mScreenPoint);
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
        Activity activity = getActivity();
        if (activity == null) return new Intent();
        return new Intent(activity, activityClass);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Activity activity = getActivity();
        if (activity == null) return;
        activity.getWindowManager().getDefaultDisplay().getSize(mScreenPoint);
    }
}
