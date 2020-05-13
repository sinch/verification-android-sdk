package com.sinch.verificationsamplejava;

import android.content.Context;

import com.facebook.flipper.android.AndroidFlipperClient;
import com.facebook.flipper.android.utils.FlipperUtils;
import com.facebook.flipper.core.FlipperClient;
import com.facebook.flipper.plugins.inspector.DescriptorMapping;
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin;
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor;
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin;
import com.facebook.soloader.SoLoader;

import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;

public class FlipperInitializer {

    static NetworkFlipperPlugin networkPlugin = new NetworkFlipperPlugin();

    static List<Interceptor> getOkHttpInterceptors() {
        return Collections.singletonList(new FlipperOkhttpInterceptor(networkPlugin));
    }

    static void initFlipperPlugins(Context context) {
        SoLoader.init(context, false);
        if (FlipperUtils.shouldEnableFlipper(context)) {
            FlipperClient instance = AndroidFlipperClient.getInstance(context);
            instance.addPlugin(new InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()));
            instance.addPlugin(networkPlugin);
            instance.start();
        }
    }

}
