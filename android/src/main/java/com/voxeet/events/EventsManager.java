package com.voxeet.events;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class EventsManager {
    private final List<AbstractEventEmitter> eventEmitters;

    public EventsManager() {
        eventEmitters = new ArrayList<>();
    }

    public void init(@NonNull EventBus eventBus, @NonNull ReactApplicationContext reactContext) {
        eventEmitters.add(new ConferenceStatusEventEmitter(reactContext, eventBus));
        eventEmitters.add(new ConferenceUserEventEmitter(reactContext, eventBus));
    }
}
