package com.voxeet.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.android.media.MediaStream;
import com.voxeet.models.ConferenceUserUtil;
import com.voxeet.models.MediaStreamUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import voxeet.com.sdk.events.success.ConferenceUserJoinedEvent;
import voxeet.com.sdk.events.success.ConferenceUserLeftEvent;
import voxeet.com.sdk.events.success.ConferenceUserQualityUpdatedEvent;
import voxeet.com.sdk.events.success.ConferenceUserUpdatedEvent;
import voxeet.com.sdk.events.success.ScreenStreamAddedEvent;
import voxeet.com.sdk.events.success.ScreenStreamRemovedEvent;
import voxeet.com.sdk.models.impl.DefaultConferenceUser;

public class ConferenceUserEventEmitter extends AbstractEventEmitter {
    public ConferenceUserEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);


        register(new EventFormatterCallback<ConferenceUserJoinedEvent>(ConferenceUserJoinedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ConferenceUserJoinedEvent instance) {
                toMap(map, instance.getUser(), instance.getMediaStream());
            }
        }).register(new EventFormatterCallback<ConferenceUserLeftEvent>(ConferenceUserLeftEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ConferenceUserLeftEvent instance) {
                toMap(map, instance.getUser());
            }
        }).register(new EventFormatterCallback<ConferenceUserUpdatedEvent>(ConferenceUserUpdatedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ConferenceUserUpdatedEvent instance) {
                toMap(map, instance.getUser(), instance.getMediaStream());
            }
        }).register(new EventFormatterCallback<ScreenStreamAddedEvent>(ScreenStreamAddedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ScreenStreamAddedEvent instance) {
                toMap(map, instance.getPeer(), instance.getMediaStream());
            }
        }).register(new EventFormatterCallback<ScreenStreamRemovedEvent>(ScreenStreamRemovedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ScreenStreamRemovedEvent instance) {
                toMap(map, instance.getPeer());
            }
        }).register(new EventFormatterCallback<ConferenceUserQualityUpdatedEvent>(ConferenceUserQualityUpdatedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ConferenceUserQualityUpdatedEvent instance) {
                toMap(map, instance.getUser());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceUserJoinedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceUserLeftEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceUserUpdatedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ScreenStreamAddedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ScreenStreamRemovedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceUserQualityUpdatedEvent event) {
        emit(event);
    }

    private void toMap(@NonNull WritableMap map, @NonNull DefaultConferenceUser user, @Nullable MediaStream mediaStream) {
        map.putMap("user", ConferenceUserUtil.toMap(user));
        if (null != mediaStream) {
            map.putMap("mediaStream", MediaStreamUtil.toMap(mediaStream));
        }
    }

    private void toMap(@NonNull WritableMap map, @NonNull DefaultConferenceUser user) {
        map.putMap("user", ConferenceUserUtil.toMap(user));
    }

    private void toMap(@NonNull WritableMap map, @NonNull String peerId, @Nullable MediaStream mediaStream) {
        map.putString("peerId", peerId);
        if (null != mediaStream) {
            map.putMap("mediaStream", MediaStreamUtil.toMap(mediaStream));
        }
    }

    private void toMap(@NonNull WritableMap map, @NonNull String peerId) {
        toMap(map, peerId, null);
    }
}
