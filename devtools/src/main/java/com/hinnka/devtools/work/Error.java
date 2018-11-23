package com.hinnka.devtools.work;

public class Error {
    public int code;
    public String message;
    public Throwable throwable;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Error(int code, String message, Throwable throwable) {
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }
}
