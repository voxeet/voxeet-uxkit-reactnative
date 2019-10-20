package com.voxeet.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.android.media.MediaStream;
import com.voxeet.models.ConferenceUserUtil;
import com.voxeet.models.MediaStreamUtil;
import com.voxeet.sdk.events.sdk.ConferenceUserQualityUpdatedEvent;
import com.voxeet.sdk.events.v2.StreamAddedEvent;
import com.voxeet.sdk.events.v2.StreamRemovedEvent;
import com.voxeet.sdk.events.v2.UserAddedEvent;
import com.voxeet.sdk.events.v2.UserUpdatedEvent;
import com.voxeet.sdk.models.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ConferenceUserEventEmitter extends AbstractEventEmitter {
    public ConferenceUserEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);


        register(new EventFormatterCallback<UserAddedEvent>(UserAddedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull UserAddedEvent instance) {
                toMap(map, instance.user);
            }
        }).register(new EventFormatterCallback<UserUpdatedEvent>(UserUpdatedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull UserUpdatedEvent instance) {
                toMap(map, instance.user);
            }
        }).register(new EventFormatterCallback<StreamAddedEvent>(StreamAddedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull StreamAddedEvent instance) {
                toMap(map, instance.user, instance.mediaStream);
            }
        }).register(new EventFormatterCallback<StreamRemovedEvent>(StreamRemovedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull StreamRemovedEvent instance) {
                toMap(map, instance.user, instance.mediaStream);
            }
        }).register(new EventFormatterCallback<ConferenceUserQualityUpdatedEvent>(ConferenceUserQualityUpdatedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ConferenceUserQualityUpdatedEvent instance) {
                toMap(map, instance.user);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserAddedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserUpdatedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StreamAddedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StreamRemovedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceUserQualityUpdatedEvent event) {
        emit(event);
    }

    private void toMap(@NonNull WritableMap map, @NonNull User user, @Nullable MediaStream mediaStream) {
        map.putMap("user", ConferenceUserUtil.toMap(user));
        if (null != mediaStream) {
            map.putMap("mediaStream", MediaStreamUtil.toMap(mediaStream));
        }
    }

    private void toMap(@NonNull WritableMap map, @NonNull User user) {
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
