package com.example.myapplication;

import android.app.Application;
import android.content.Context;

public class App  extends Application {

    private static App sApp;

    public static App get() {
        return sApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sApp = this;
    }
}
