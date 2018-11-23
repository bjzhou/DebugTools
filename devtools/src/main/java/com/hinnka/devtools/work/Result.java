package com.hinnka.devtools.work;

public class Result<T> {
    public T data;
    public Error error;
    public boolean retry;

    public Result(T data) {
        this.data = data;
    }

    public Result(Error error, T data) {
        this.data = data;
        this.error = error;
    }

    public Result(Error error, boolean retry, T data) {
        this.data = data;
        this.error = error;
        this.retry = retry;
    }
}
