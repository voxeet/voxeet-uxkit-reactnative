package com.voxeet.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.android.media.MediaStream;
import com.voxeet.models.ConferenceUserUtil;
import com.voxeet.models.MediaStreamUtil;
import com.voxeet.sdk.events.error.ParticipantAddedErrorEvent;
import com.voxeet.sdk.events.sdk.ConferenceParticipantQualityUpdatedEvent;
import com.voxeet.sdk.events.v2.ParticipantAddedEvent;
import com.voxeet.sdk.events.v2.ParticipantUpdatedEvent;
import com.voxeet.sdk.events.v2.StreamAddedEvent;
import com.voxeet.sdk.events.v2.StreamRemovedEvent;
import com.voxeet.sdk.models.Participant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ConferenceUserEventEmitter extends AbstractEventEmitter {
    public ConferenceUserEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);


        register(new EventFormatterCallback<ParticipantAddedEvent>(ParticipantAddedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ParticipantAddedEvent instance) {
                toMap(map, instance.participant);
            }
        }).register(new EventFormatterCallback<ParticipantUpdatedEvent>(ParticipantUpdatedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ParticipantUpdatedEvent instance) {
                toMap(map, instance.participant);
            }
        }).register(new EventFormatterCallback<StreamAddedEvent>(StreamAddedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull StreamAddedEvent instance) {
                toMap(map, instance.participant, instance.mediaStream);
            }
        }).register(new EventFormatterCallback<StreamRemovedEvent>(StreamRemovedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull StreamRemovedEvent instance) {
                toMap(map, instance.participant, instance.mediaStream);
            }
        }).register(new EventFormatterCallback<ConferenceParticipantQualityUpdatedEvent>(ConferenceParticipantQualityUpdatedEvent.class) {
            @Override
            public void transform(@NonNull WritableMap map, @NonNull ConferenceParticipantQualityUpdatedEvent instance) {
                toMap(map, instance.participant);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ParticipantAddedEvent event) {
        emit(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ParticipantUpdatedEvent event) {
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
    public void onEvent(ConferenceParticipantQualityUpdatedEvent event) {
        emit(event);
    }

    private void toMap(@NonNull WritableMap map, @NonNull Participant user, @Nullable MediaStream mediaStream) {
        map.putMap("user", ConferenceUserUtil.toMap(user));
        if (null != mediaStream) {
            map.putMap("mediaStream", MediaStreamUtil.toMap(mediaStream));
        }
    }

    private void toMap(@NonNull WritableMap map, @NonNull Participant user) {
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
