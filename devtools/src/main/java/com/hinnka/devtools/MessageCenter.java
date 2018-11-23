package com.hinnka.devtools;

import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class MessageCenter implements Observer<Object> {

    private static final String TAG = MessageCenter.class.getSimpleName();
    private final PublishSubject<Object> messageSubject;
    private final LruCache<Class, Object> messageStack = new LruCache<>(10);
    private final ConcurrentHashMap<Class, CopyOnWriteArraySet<MessageObserver>> observerMap = new ConcurrentHashMap<>();

    private MessageCenter() {
        messageSubject = PublishSubject.create();
        messageSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    }

    public void send(Object message) {
        messageSubject.onNext(message);
    }

    public void push(Object message) {
        messageStack.put(message.getClass(), message);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T peek(Class<T> clz) {
        return (T) messageStack.get(clz);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T pop(Class<T> clz) {
        return (T) messageStack.remove(clz);
    }

    public void observeMessage(MessageObserver<Object> messageObserver) {
        observeMessage(messageObserver, Object.class);
    }

    public <T> void observeMessage(MessageObserver<T> messageObserver, Class<T> clz) {
        CopyOnWriteArraySet<MessageObserver> observers = observerMap.get(clz);
        if (observers == null) {
            observers = new CopyOnWriteArraySet<>();
            observerMap.put(clz, observers);
        }
        observers.add(messageObserver);
    }

    public void removeObserver(MessageObserver observer) {
        for (CopyOnWriteArraySet<MessageObserver> messageObservers : observerMap.values()) {
            messageObservers.remove(observer);
        }
    }

    public void removeObserverByClass(Class clz) {
        CopyOnWriteArraySet<MessageObserver> observers = observerMap.get(clz);
        if (observers != null) {
            observers.clear();
        }
    }

    public void clear() {
        for (CopyOnWriteArraySet<MessageObserver> messageObservers : observerMap.values()) {
            messageObservers.clear();
        }
        observerMap.clear();
    }

    public static MessageCenter get() {
        return Holder.INSTANCE;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNext(Object message) {
        for (Map.Entry<Class, CopyOnWriteArraySet<MessageObserver>> entry : observerMap.entrySet()) {
            if (entry.getKey().isInstance(message)) {
                for (MessageObserver messageObserver : entry.getValue()) {
                    try {
                        messageObserver.onReceiveMessage(message);
                    } catch (Exception e) {
                        onError(e);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        L.e(TAG, "observeError", e);
    }

    @Override
    public void onComplete() {
        L.d(TAG, "observeComplete");
    }

    private static class Holder {
        private static final MessageCenter INSTANCE = new MessageCenter();
    }

    public interface MessageObserver<T> {
        void onReceiveMessage(T message);
    }
}
