package com.voxeet.events;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.sdk.json.FilePresentationStarted;
import com.voxeet.sdk.json.FilePresentationStopped;
import com.voxeet.sdk.json.FilePresentationUpdated;
import com.voxeet.sdk.json.VideoPresentationPaused;
import com.voxeet.sdk.json.VideoPresentationPlay;
import com.voxeet.sdk.json.VideoPresentationSeek;
import com.voxeet.sdk.json.VideoPresentationStarted;
import com.voxeet.sdk.json.VideoPresentationStopped;
import com.voxeet.sdk.models.v1.FilePresentationConverted;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class VideoPresentationEventEmitter extends AbstractEventEmitter {
    public VideoPresentationEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);

        register(new EventFormatterCallback<VideoPresentationSeek>(VideoPresentationSeek.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull VideoPresentationSeek event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("key", event.key);
                map.putString("participantId", event.participantId);
                map.putInt("timestamp", (int) event.timestamp);
            }
        }).register(new EventFormatterCallback<VideoPresentationPlay>(VideoPresentationPlay.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull VideoPresentationPlay event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("key", event.key);
                map.putString("participantId", event.participantId);
                map.putInt("timestamp", (int) event.timestamp);
            }
        }).register(new EventFormatterCallback<VideoPresentationStopped>(VideoPresentationStopped.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull VideoPresentationStopped event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("key", event.key);
                map.putString("participantId", event.participantId);
            }
        }).register(new EventFormatterCallback<VideoPresentationPaused>(VideoPresentationPaused.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull VideoPresentationPaused event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("key", event.key);
                map.putString("participantId", event.participantId);
                map.putInt("timestamp", (int) event.timestamp);
            }
        }).register(new EventFormatterCallback<VideoPresentationStarted>(VideoPresentationStarted.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull VideoPresentationStarted event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("key", event.key);
                map.putString("participantId", event.participantId);
                map.putInt("timestamp", (int) event.timestamp);
                map.putString("url", event.url);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FilePresentationConverted event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FilePresentationStarted event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FilePresentationStopped event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FilePresentationUpdated event) {
        emit(event);
    }
}
