package com.sinch.verificationsamplejava;

import android.app.Application;

import com.sinch.logging.Log;
import com.sinch.logging.LogcatAppender;

public class VerificationJavaSampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initFlipper();
        initLogger();
    }

    private void initFlipper() {
        FlipperInitializer.initFlipperPlugins(this);
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {
            Log.init(new LogcatAppender());
        }
    }

}
