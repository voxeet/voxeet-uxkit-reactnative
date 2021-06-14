package com.voxeet.reactnative.events;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.sdk.json.FilePresentationStarted;
import com.voxeet.sdk.json.FilePresentationStopped;
import com.voxeet.sdk.json.FilePresentationUpdated;
import com.voxeet.sdk.models.v1.FilePresentationConverted;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FilePresentationEventEmitter extends AbstractEventEmitter {
    public FilePresentationEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);

        register(new EventFormatterCallback<FilePresentationConverted>(FilePresentationConverted.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull FilePresentationConverted event) {
                map.putInt("nbImageConverted", event.nbImageConverted);
                map.putString("fileId", event.fileId);
                map.putString("name", event.name);
                map.putInt("size", (int) event.size);
            }
        }).register(new EventFormatterCallback<FilePresentationStarted>(FilePresentationStarted.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull FilePresentationStarted event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("fileId", event.fileId);
                map.putString("participantId", event.userId);
                map.putInt("position", event.position);
                map.putInt("imageCount", event.imageCount);
            }
        }).register(new EventFormatterCallback<FilePresentationStopped>(FilePresentationStopped.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull FilePresentationStopped event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("fileId", event.fileId);
                map.putString("participantId", event.userId);
            }
        }).register(new EventFormatterCallback<FilePresentationUpdated>(FilePresentationUpdated.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull FilePresentationUpdated event) {
                map.putString("conferenceId", event.conferenceId);
                map.putString("fileId", event.fileId);
                map.putString("participantId", event.userId);
                map.putInt("position", event.position);
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
