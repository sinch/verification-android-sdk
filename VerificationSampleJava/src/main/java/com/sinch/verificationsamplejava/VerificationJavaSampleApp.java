package com.sinch.verificationsamplejava;

import android.app.Application;

import com.facebook.flipper.android.AndroidFlipperClient;
import com.facebook.flipper.android.utils.FlipperUtils;
import com.facebook.flipper.core.FlipperClient;
import com.facebook.flipper.plugins.inspector.DescriptorMapping;
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin;
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin;
import com.facebook.soloader.SoLoader;
import com.sinch.logging.Log;
import com.sinch.logging.LogcatAppender;

public class VerificationJavaSampleApp extends Application {

    NetworkFlipperPlugin networkFlipperPlugin = new NetworkFlipperPlugin();

    @Override
    public void onCreate() {
        super.onCreate();
        initFlipper();
        initLogger();
    }

    private void initFlipper() {
        SoLoader.init(this, false);
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            FlipperClient instance = AndroidFlipperClient.getInstance(this);
            instance.addPlugin(new InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()));
            instance.addPlugin(networkFlipperPlugin);
            instance.start();
        }
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {
            Log.init(new LogcatAppender());
        }
    }

}
