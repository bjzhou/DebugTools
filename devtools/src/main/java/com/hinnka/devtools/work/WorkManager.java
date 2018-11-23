package com.hinnka.devtools.work;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("unchecked")
public class WorkManager {

    private List<Function<?, ObservableSource<?>>> mWorkers = new ArrayList<>();

    private Consumer<Throwable> mError = throwable -> {};
    private Action mFinally = () -> {};
    private Action mComplete = () -> {};
    private Consumer mNext = o -> {};

    public WorkManager() {
    }

    public static WorkManager begin() {
        return new WorkManager();
    }

    public static WorkManager begin(ObservableSource observable) {
        return new WorkManager().then(observable);
    }

    public WorkManager then(ObservableSource worker) {
        mWorkers.add(o -> worker);
        return this;
    }

    public WorkManager then(Function<?, ObservableSource<?>> function) {
        mWorkers.add(function);
        return this;
    }

    public WorkManager then(UIWorker worker) {
        mWorkers.add(o -> Observable.fromCallable(() -> worker.doWork(o)).subscribeOn(AndroidSchedulers.mainThread()));
        return this;
    }

    public WorkManager then(Worker worker) {
        mWorkers.add(o -> Observable.fromCallable(() -> worker.doWork(o)).subscribeOn(Schedulers.io()));
        return this;
    }

    public WorkManager whenNext(Consumer<?> consumer) {
        mNext = consumer;
        return this;
    }

    public WorkManager whenError(Consumer<Throwable> consumer) {
        mError = consumer;
        return this;
    }

    public WorkManager whenComplete(Action action) {
        mComplete = action;
        return this;
    }

    public WorkManager doFinally(Action action) {
        mFinally = action;
        return this;
    }

    public Disposable enqueue() {
        if (mWorkers.isEmpty()) {
            return new EmptyDisposable();
        }
        Observable observable = Observable.just("").subscribeOn(Schedulers.io());
        for (Function worker : mWorkers) {
            observable = observable.flatMap(worker);
        }
        mWorkers.clear();
        return observable.doFinally(mFinally).subscribe(mNext, mError, mComplete);
    }

    public class EmptyDisposable implements Disposable {

        @Override
        public void dispose() {

        }

        @Override
        public boolean isDisposed() {
            return true;
        }
    }
}
