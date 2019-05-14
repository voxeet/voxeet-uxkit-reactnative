
package com.voxeet;

import android.support.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.voxeet.events.AbstractEventEmitter;
import com.voxeet.events.ConferenceStatusEventEmitter;
import com.voxeet.events.ConferenceUserEventEmitter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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