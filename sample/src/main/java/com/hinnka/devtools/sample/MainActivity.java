package com.hinnka.devtools.sample;

import android.view.View;

import com.hinnka.devtools.HinnkaActivity;
import com.hinnka.devtools.L;
import com.hinnka.devtools.MessageCenter;
import com.hinnka.devtools.sample.databinding.ActivityMainBinding;
import com.hinnka.devtools.work.UIWorker;
import com.hinnka.devtools.work.WorkManager;
import com.hinnka.devtools.work.Worker;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends HinnkaActivity implements MessageCenter.MessageObserver<String> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewLoad() {
        ActivityMainBinding binding = getBinding(ActivityMainBinding.class);
        binding.button.setText("Hello DevTools!");
        binding.button.setOnClickListener(v -> {
            binding.button.setVisibility(View.GONE);
            binding.fragmentLayout.setVisibility(View.VISIBLE);
            delay(() -> {
                send("Hello MessageCenter");
            }, 1000);
        });
        MessageCenter.get().observeMessage(this, String.class);

        Observable o1 = Observable.fromCallable((Callable<Object>) () -> {
            L.d("Hinnka", "o1", Thread.currentThread().getName());
            return "1";
        }).subscribeOn(AndroidSchedulers.mainThread());

        Observable o2 = Observable.fromCallable(() -> {
            L.d("Hinnka", "o2", Thread.currentThread().getName());
            return "2";
        }).subscribeOn(Schedulers.io());

        Function<?, ObservableSource<?>> f3 = s -> Observable.fromCallable(() -> {
            L.d("Hinnka", "o3", s, Thread.currentThread().getName());
            return s + "1";
        }).subscribeOn(AndroidSchedulers.mainThread());

        Worker<String, String> w4 = s -> {
            L.d("Hinnka", "o4", s, Thread.currentThread().getName());
            return s + "1";
        };

        UIWorker<String, String> w5 = s -> {
            L.d("Hinnka", "o4", s, Thread.currentThread().getName());
            return s + "1";
        };

        WorkManager.begin(o1)
                .then(o2)
                .then(f3)
                .then(w4)
                .then(w5)
                .enqueue();
    }

    @Override
    protected void onViewVisible() {
    }

    @Override
    public void onReceiveMessage(String message) {
        ActivityMainBinding binding = getBinding(ActivityMainBinding.class);
        binding.button.setVisibility(View.VISIBLE);
        binding.fragmentLayout.setVisibility(View.GONE);
        binding.button.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageCenter.get().removeObserver(this);
    }
}
