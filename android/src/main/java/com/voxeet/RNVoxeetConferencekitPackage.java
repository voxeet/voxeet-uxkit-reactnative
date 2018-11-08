
package com.voxeet;

import android.app.Application;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.voxeet.notification.RNIncomingCallActivity;
import com.voxeet.specifics.RNRootViewProvider;
import com.voxeet.toolkit.controllers.VoxeetToolkit;
import com.voxeet.toolkit.implementation.overlays.OverlayState;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import voxeet.com.sdk.core.preferences.VoxeetPreferences;

public class RNVoxeetConferencekitPackage implements ReactPackage {

    private final RNRootViewProvider mRNRootViewProvider;

    public RNVoxeetConferencekitPackage(Application application) {

        VoxeetToolkit.initialize(application, EventBus.getDefault());

        mRNRootViewProvider = new RNRootViewProvider(application, VoxeetToolkit.getInstance());
        VoxeetToolkit.getInstance().setProvider(mRNRootViewProvider);

        VoxeetToolkit.getInstance().enableOverlay(true);

        //force a default voxeet preferences manager
        //in sdk mode, no issues
        VoxeetPreferences.init(application);
        //deprecated but we can only use it using the cordova plugin - for now
        VoxeetPreferences.setDefaultActivity(RNIncomingCallActivity.class.getCanonicalName());

        //change the overlay used by default
        VoxeetToolkit.getInstance().getConferenceToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
        VoxeetToolkit.getInstance().getReplayMessageToolkit().setDefaultOverlayState(OverlayState.EXPANDED);

    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new RNVoxeetConferencekitModule(reactContext));
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}