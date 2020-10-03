package com.example.base;

import android.app.Application;

import com.example.xroute_core.XRouter;

public class XRouterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XRouter.Companion.init(this);
    }
}
