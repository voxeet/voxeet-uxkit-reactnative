package com.voxeet.events;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.sdk.events.error.CameraSwitchErrorEvent;
import com.voxeet.sdk.events.error.GetConferenceHistoryErrorEvent;
import com.voxeet.sdk.events.error.GetConferenceStatusErrorEvent;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.error.ReplayConferenceErrorEvent;
import com.voxeet.sdk.events.error.SdkLogoutErrorEvent;
import com.voxeet.sdk.events.error.UnsubscribeFromCallConferenceErrorEvent;
import com.voxeet.sdk.events.promises.PromiseParticipantAddedErrorEventException;
import com.voxeet.sdk.events.sdk.CameraSwitchSuccessEvent;
import com.voxeet.sdk.events.sdk.ConferenceStateEvent;
import com.voxeet.sdk.events.sdk.IncomingCallEvent;
import com.voxeet.sdk.events.sdk.QualityIndicators;
import com.voxeet.sdk.events.sdk.SdkLogoutSuccessEvent;
import com.voxeet.sdk.events.sdk.StartScreenShareAnswerEvent;
import com.voxeet.sdk.events.sdk.StopScreenShareAnswerEvent;
import com.voxeet.sdk.events.sdk.StopVideoAnswerEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.sdk.json.ConferenceEnded;
import com.voxeet.sdk.json.RecordingStatusUpdatedEvent;
import com.voxeet.sdk.json.StartVideoResponse;

import org.greenrobot.eventbus.EventBus;

public class ConferenceStatusEventEmitter extends AbstractEventEmitter {

    public ConferenceStatusEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);

        register(new EventFormatterCallback<PermissionRefusedEvent>(PermissionRefusedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull PermissionRefusedEvent instance) {
                map.putString("permission", instance.getPermission().name());
            }
        }).register(new EventFormatterCallback<PromiseParticipantAddedErrorEventException>(PromiseParticipantAddedErrorEventException.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull PromiseParticipantAddedErrorEventException instance) {
                map.putString("message", instance.getEvent().message());
            }
        }).register(new EventFormatterCallback<SdkLogoutSuccessEvent>(SdkLogoutSuccessEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SdkLogoutSuccessEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<SdkLogoutErrorEvent>(SdkLogoutErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SdkLogoutErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<ReplayConferenceErrorEvent>(ReplayConferenceErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ReplayConferenceErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<ConferenceStateEvent>(ConferenceStateEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceStateEvent instance) {
                map.putString("conferenceId", null != instance.conference ? instance.conference.getId() : null);
                map.putString("conferenceAlias", instance.conferenceAlias);
                map.putString("state", instance.state.name());
            }
        }).register(new EventFormatterCallback<GetConferenceStatusErrorEvent>(GetConferenceStatusErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull GetConferenceStatusErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<GetConferenceHistoryErrorEvent>(GetConferenceHistoryErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull GetConferenceHistoryErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<CameraSwitchSuccessEvent>(CameraSwitchSuccessEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull CameraSwitchSuccessEvent instance) {
                map.putBoolean("isFront", instance.isFront);
            }
        }).register(new EventFormatterCallback<CameraSwitchErrorEvent>(CameraSwitchErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull CameraSwitchErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<UnsubscribeFromCallConferenceErrorEvent>(UnsubscribeFromCallConferenceErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull UnsubscribeFromCallConferenceErrorEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        }).register(new EventFormatterCallback<QualityIndicators>(QualityIndicators.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull QualityIndicators instance) {
                map.putDouble("mos", instance.mos);
            }
        }).register(new EventFormatterCallback<IncomingCallEvent>(IncomingCallEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull IncomingCallEvent instance) {
                map.putString("conferenceId", instance.conferenceId);
            }
        }).register(new EventFormatterCallback<RecordingStatusUpdatedEvent>(RecordingStatusUpdatedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull RecordingStatusUpdatedEvent instance) {
                map.putString("conferenceId", instance.conferenceId);
                map.putString("userId", instance.userId);
                map.putString("recordingStatus", instance.recordingStatus);
                map.putString("type", instance.getType());
            }
        }).register(new EventFormatterCallback<ConferenceDestroyedPush>(ConferenceDestroyedPush.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceDestroyedPush instance) {
                map.putString("conferenceId", instance.conferenceId);
            }
        }).register(new EventFormatterCallback<ConferenceEnded>(ConferenceEnded.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceEnded instance) {
                map.putString("conferenceId", instance.conferenceId);
            }
        });
    }

    public void onEvent(ConferenceStateEvent event) {
        emit(event);
    }

    public void onEvent(StartScreenShareAnswerEvent event) {
        emit(event);
    }

    public void onEvent(StopScreenShareAnswerEvent event) {
        emit(event);
    }

    public void onEvent(PermissionRefusedEvent event) {
        emit(event);
    }

    public void onEvent(StartVideoResponse event) {
        emit(event);
    }

    public void onEvent(StopVideoAnswerEvent event) {
        emit(event);
    }

    public void onEvent(PromiseParticipantAddedErrorEventException event) {
        emit(event);
    }

    public void onEvent(SdkLogoutSuccessEvent event) {
        emit(event);
    }

    public void onEvent(SdkLogoutErrorEvent event) {
        emit(event);
    }

    public void onEvent(ReplayConferenceErrorEvent event) {
        emit(event);
    }

    public void onEvent(GetConferenceStatusErrorEvent event) {
        emit(event);
    }

    public void onEvent(GetConferenceHistoryErrorEvent event) {
        emit(event);
    }

    public void onEvent(CameraSwitchSuccessEvent event) {
        emit(event);
    }

    public void onEvent(CameraSwitchErrorEvent event) {
        emit(event);
    }

    public void onEvent(QualityIndicators event) {
        emit(event);
    }

    public void onEvent(IncomingCallEvent event) {
        emit(event);
    }

    public void onEvent(RecordingStatusUpdatedEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceDestroyedPush event) {
        emit(event);
    }

    public void onEvent(ConferenceEnded event) {
        emit(event);
    }
}
