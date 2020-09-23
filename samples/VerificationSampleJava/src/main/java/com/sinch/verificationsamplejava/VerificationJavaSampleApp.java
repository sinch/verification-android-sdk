package com.sinch.verificationsamplejava;

import android.app.Application;

import com.sinch.logging.Log;
import com.sinch.logging.LogcatAppender;
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod;
import com.sinch.verification.core.config.general.GlobalConfig;
import com.sinch.verification.core.config.general.SinchGlobalConfig;

public class VerificationJavaSampleApp extends Application {

    private GlobalConfig globalConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        initFlipper();
        initLogger();
        globalConfig = SinchGlobalConfig.Builder.getInstance()
                .applicationContext(this)
                .authorizationMethod(new AppKeyAuthorizationMethod(BuildConfig.APP_KEY))
                .build();
    }

    private void initFlipper() {
        FlipperInitializer.initFlipperPlugins(this);
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {
            Log.init(new LogcatAppender());
        }
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

}
