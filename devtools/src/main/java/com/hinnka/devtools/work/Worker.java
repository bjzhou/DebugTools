package com.hinnka.devtools.work;

public interface Worker<T, R> {
    R doWork(T t);
}
