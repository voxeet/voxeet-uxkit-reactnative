
package com.voxeet;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.voxeet.events.AbstractEventEmitter;
import com.voxeet.events.ConferenceStatusEventEmitter;
import com.voxeet.events.ConferenceUserEventEmitter;
import com.voxeet.notification.RNIncomingBundleChecker;
import com.voxeet.notification.RNIncomingCallActivity;
import com.voxeet.toolkit.controllers.VoxeetToolkit;
import com.voxeet.toolkit.implementation.overlays.OverlayState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.core.FirebaseController;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.preferences.VoxeetPreferences;
import voxeet.com.sdk.events.error.PermissionRefusedEvent;
import voxeet.com.sdk.events.success.ConferenceRefreshedEvent;
import voxeet.com.sdk.events.success.SocketConnectEvent;
import voxeet.com.sdk.events.success.SocketStateChangeEvent;
import voxeet.com.sdk.json.UserInfo;
import voxeet.com.sdk.json.internal.MetadataHolder;
import voxeet.com.sdk.json.internal.ParamsHolder;
import voxeet.com.sdk.models.ConferenceResponse;

public class RNVoxeetConferenceEventModule extends ReactContextBaseJavaModule {

    private final static String TAG = RNVoxeetConferenceEventModule.class.getSimpleName();

    private final ReactApplicationContext reactContext;
    private final List<AbstractEventEmitter> eventEmitters;

    public RNVoxeetConferenceEventModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;


        eventEmitters = new ArrayList<>();
        eventEmitters.add(new ConferenceStatusEventEmitter(reactContext,
                EventBus.getDefault()));
        eventEmitters.add(new ConferenceUserEventEmitter(reactContext,
                EventBus.getDefault()));

        register(null);
    }

    @Override
    public String getName() {
        return "RNVoxeetConferenceEvent";
    }

    @ReactMethod
    public void infos(Promise promise) {
        promise.resolve("this module only manage the events");
    }

    @ReactMethod
    public void register(@Nullable Promise promise) {
        for (AbstractEventEmitter emitter : eventEmitters) {
            emitter.register();
        }

        if(null != promise) {
            promise.resolve(true);
        }
    }

    @ReactMethod
    public void unRegister(@Nullable Promise promise) {
        for (AbstractEventEmitter emitter : eventEmitters) {
            emitter.unRegister();
        }

        if(null != promise) {
            promise.resolve(true);
        }
    }

}