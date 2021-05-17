package com.voxeet.reactnative.events;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.reactnative.video.RNVideoViewInternalEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class VideoViewEventEmitter extends AbstractEventEmitter {
    public VideoViewEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);

        register(new EventFormatterCallback<RNVideoViewInternalEvent>(RNVideoViewInternalEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull RNVideoViewInternalEvent event) {
                map.putInt("requestId", event.requestId);
                map.putBoolean("result", event.result);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RNVideoViewInternalEvent event) {
        emit(event);
    }

}
