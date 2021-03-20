package com.example.xrouter_core.utils;

import android.util.Log;

public class Logger {
    public static Logger logger = new Logger();
    private final String tag = "XRouter";
    public void i(String message) {
        Log.i(tag, message);
    }

    public void d(String message) {
        Log.d(tag, message);
    }
    public void e(String message) {
        Log.e(tag, message);
    }
    public void w(String message) {
        Log.w(tag, message);
    }
}
