package com.sinch.verification.sample;

import android.app.Application;

import com.sinch.logging.Log;
import com.sinch.logging.LogcatAppender;

public class LegacySampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Log.init(new LogcatAppender());
        }
    }

}
