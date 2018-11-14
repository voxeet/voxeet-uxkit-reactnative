package com.voxeet.events;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.voxeet.models.ConferenceUserUtil;
import com.voxeet.models.ConferenceUtil;

import org.greenrobot.eventbus.EventBus;

import voxeet.com.sdk.events.error.CameraSwitchErrorEvent;
import voxeet.com.sdk.events.error.ConferenceCreatedError;
import voxeet.com.sdk.events.error.ConferenceJoinedError;
import voxeet.com.sdk.events.error.ConferenceLeftError;
import voxeet.com.sdk.events.error.GetConferenceHistoryErrorEvent;
import voxeet.com.sdk.events.error.GetConferenceStatusErrorEvent;
import voxeet.com.sdk.events.error.PermissionRefusedEvent;
import voxeet.com.sdk.events.error.ReplayConferenceErrorEvent;
import voxeet.com.sdk.events.error.SdkLogoutErrorEvent;
import voxeet.com.sdk.events.error.SubscribeConferenceErrorEvent;
import voxeet.com.sdk.events.error.SubscribeForCallConferenceErrorEvent;
import voxeet.com.sdk.events.error.UnsubscribeFromCallConferenceErrorEvent;
import voxeet.com.sdk.events.promises.PromiseParticipantAddedErrorEventException;
import voxeet.com.sdk.events.success.AddConferenceParticipantResultEvent;
import voxeet.com.sdk.events.success.CameraSwitchSuccessEvent;
import voxeet.com.sdk.events.success.ConferenceCreationSuccess;
import voxeet.com.sdk.events.success.ConferenceDestroyedPushEvent;
import voxeet.com.sdk.events.success.ConferenceEndedEvent;
import voxeet.com.sdk.events.success.ConferenceJoinedSuccessEvent;
import voxeet.com.sdk.events.success.ConferenceLeftSuccessEvent;
import voxeet.com.sdk.events.success.ConferencePreJoinedEvent;
import voxeet.com.sdk.events.success.ConferenceRefreshedEvent;
import voxeet.com.sdk.events.success.ConferenceStatsEvent;
import voxeet.com.sdk.events.success.ConferenceUserCallDeclinedEvent;
import voxeet.com.sdk.events.success.ConferenceUsersInvitedEvent;
import voxeet.com.sdk.events.success.DeclineConferenceResultEvent;
import voxeet.com.sdk.events.success.GetConferenceHistoryEvent;
import voxeet.com.sdk.events.success.GetConferenceStatusEvent;
import voxeet.com.sdk.events.success.IncomingCallEvent;
import voxeet.com.sdk.events.success.QualityIndicators;
import voxeet.com.sdk.events.success.SdkLogoutSuccessEvent;
import voxeet.com.sdk.events.success.SendBroadcastResultEvent;
import voxeet.com.sdk.events.success.StartRecordingResultEvent;
import voxeet.com.sdk.events.success.StartScreenShareAnswerEvent;
import voxeet.com.sdk.events.success.StartVideoAnswerEvent;
import voxeet.com.sdk.events.success.StopRecordingResultEvent;
import voxeet.com.sdk.events.success.StopScreenShareAnswerEvent;
import voxeet.com.sdk.events.success.StopVideoAnswerEvent;
import voxeet.com.sdk.events.success.SubscribeConferenceEvent;
import voxeet.com.sdk.events.success.SubscribeForCallConferenceAnswerEvent;
import voxeet.com.sdk.events.success.UnSubscribeConferenceAnswerEvent;
import voxeet.com.sdk.events.success.UnSubscribeFromConferenceAnswerEvent;
import voxeet.com.sdk.json.ConferenceDestroyedPush;
import voxeet.com.sdk.json.ConferenceEnded;
import voxeet.com.sdk.json.RecordingStatusUpdateEvent;

public class ConferenceStatusEventEmitter extends AbstractEventEmitter {

    public ConferenceStatusEventEmitter(@NonNull ReactContext context, @NonNull EventBus eventBus) {
        super(context, eventBus);

        register(new EventFormatterCallback<ConferenceCreatedError>(ConferenceCreatedError.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceCreatedError instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<DeclineConferenceResultEvent>(DeclineConferenceResultEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull DeclineConferenceResultEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<StartScreenShareAnswerEvent>(StartScreenShareAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull StartScreenShareAnswerEvent instance) {
                map.putBoolean("isAlreadyStarted", instance.isAlreadyStarted());
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<StopScreenShareAnswerEvent>(StopScreenShareAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull StopScreenShareAnswerEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<PermissionRefusedEvent>(PermissionRefusedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull PermissionRefusedEvent instance) {
                map.putString("permission", instance.getPermission().name());
            }
        }).register(new EventFormatterCallback<StartVideoAnswerEvent>(StartVideoAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull StartVideoAnswerEvent instance) {
                map.putBoolean("isAlreadyStarted", instance.isAlreadyStarted());
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<StopVideoAnswerEvent>(StopVideoAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull StopVideoAnswerEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<PromiseParticipantAddedErrorEventException>(PromiseParticipantAddedErrorEventException.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull PromiseParticipantAddedErrorEventException instance) {
                map.putString("message", instance.getEvent().message());
            }
        }).register(new EventFormatterCallback<ConferenceCreationSuccess>(ConferenceCreationSuccess.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceCreationSuccess instance) {
                map.putString("conferenceId", instance.getConfId());
                map.putString("conferenceAlias", instance.getConfAlias());
            }
        }).register(new EventFormatterCallback<AddConferenceParticipantResultEvent>(AddConferenceParticipantResultEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull AddConferenceParticipantResultEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<ConferenceRefreshedEvent>(ConferenceRefreshedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceRefreshedEvent instance) {
                map.putMap("user", ConferenceUserUtil.toMap(instance.getUser()));
                map.putString("userId", instance.getUserId());
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
        }).register(new EventFormatterCallback<StartRecordingResultEvent>(StartRecordingResultEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull StartRecordingResultEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<StopRecordingResultEvent>(StopRecordingResultEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull StopRecordingResultEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<GetConferenceStatusEvent>(GetConferenceStatusEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull GetConferenceStatusEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
                map.putString("conferenceAlias", instance.getAliasId());
                map.putString("type", instance.getType());
            }
        }).register(new EventFormatterCallback<GetConferenceStatusErrorEvent>(GetConferenceStatusErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull GetConferenceStatusErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<GetConferenceHistoryEvent>(GetConferenceHistoryEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull GetConferenceHistoryEvent instance) {
                map.putArray("histories", ConferenceUtil.toMap(instance.getItems()));
            }
        }).register(new EventFormatterCallback<GetConferenceHistoryErrorEvent>(GetConferenceHistoryErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull GetConferenceHistoryErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<CameraSwitchSuccessEvent>(CameraSwitchSuccessEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull CameraSwitchSuccessEvent instance) {
                map.putBoolean("isFront", instance.isFront());
            }
        }).register(new EventFormatterCallback<CameraSwitchErrorEvent>(CameraSwitchErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull CameraSwitchErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<SubscribeConferenceEvent>(SubscribeConferenceEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SubscribeConferenceEvent instance) {
                map.putMap("conference", ConferenceUtil.toMap(instance.getSubscribeConference()));
            }
        }).register(new EventFormatterCallback<SubscribeConferenceErrorEvent>(SubscribeConferenceErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SubscribeConferenceErrorEvent instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<UnSubscribeConferenceAnswerEvent>(UnSubscribeConferenceAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull UnSubscribeConferenceAnswerEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<SubscribeForCallConferenceAnswerEvent>(SubscribeForCallConferenceAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SubscribeForCallConferenceAnswerEvent instance) {
                map.putString("conferenceId", instance.getSubscribeConference());
            }
        }).register(new EventFormatterCallback<SubscribeForCallConferenceErrorEvent>(SubscribeForCallConferenceErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SubscribeForCallConferenceErrorEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        }).register(new EventFormatterCallback<UnSubscribeFromConferenceAnswerEvent>(UnSubscribeFromConferenceAnswerEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull UnSubscribeFromConferenceAnswerEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        }).register(new EventFormatterCallback<UnsubscribeFromCallConferenceErrorEvent>(UnsubscribeFromCallConferenceErrorEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull UnsubscribeFromCallConferenceErrorEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        }).register(new EventFormatterCallback<ConferencePreJoinedEvent>(ConferencePreJoinedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferencePreJoinedEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
                map.putString("conferenceAlias", instance.getAliasId());
            }
        }).register(new EventFormatterCallback<ConferenceJoinedError>(ConferenceJoinedError.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceJoinedError instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<ConferenceJoinedSuccessEvent>(ConferenceJoinedSuccessEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceJoinedSuccessEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
                map.putString("conferenceAlias", instance.getAliasId());
            }
        }).register(new EventFormatterCallback<ConferenceUsersInvitedEvent>(ConferenceUsersInvitedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceUsersInvitedEvent instance) {
                map.putArray("conferenceUsers", ConferenceUserUtil.toMap(instance.getConferenceUsers()));
            }
        }).register(new EventFormatterCallback<SendBroadcastResultEvent>(SendBroadcastResultEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull SendBroadcastResultEvent instance) {
                map.putBoolean("isSuccess", instance.isSuccess());
            }
        }).register(new EventFormatterCallback<ConferenceLeftSuccessEvent>(ConferenceLeftSuccessEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceLeftSuccessEvent instance) {
                map.putInt("remainingUsers", instance.getRemainingUsers());
            }
        }).register(new EventFormatterCallback<ConferenceLeftError>(ConferenceLeftError.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceLeftError instance) {
                map.putString("message", instance.message());
            }
        }).register(new EventFormatterCallback<QualityIndicators>(QualityIndicators.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull QualityIndicators instance) {
                map.putDouble("mos", instance.getMos());
            }
        }).register(new EventFormatterCallback<ConferenceStatsEvent>(ConferenceStatsEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceStatsEvent instance) {
                map.putString("conferenceId", instance.getEvent().getConference_id());
            }
        }).register(new EventFormatterCallback<IncomingCallEvent>(IncomingCallEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull IncomingCallEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        }).register(new EventFormatterCallback<RecordingStatusUpdateEvent>(RecordingStatusUpdateEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull RecordingStatusUpdateEvent instance) {
                map.putString("conferenceId", instance.getConferenceId());
                map.putString("userId", instance.getUserId());
                map.putString("recordingStatus", instance.getRecordingStatus());
                map.putString("type", instance.getType());
            }
        }).register(new EventFormatterCallback<ConferenceUserCallDeclinedEvent>(ConferenceUserCallDeclinedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceUserCallDeclinedEvent instance) {
                map.putString("conferenceId", instance.getConfId());
                map.putString("userId", instance.getUserId());
            }
        }).register(new EventFormatterCallback<ConferenceDestroyedPushEvent>(ConferenceDestroyedPushEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceDestroyedPushEvent instance) {
                map.putString("conferenceId", instance.getPush().getConferenceId());
            }
        }).register(new EventFormatterCallback<ConferenceDestroyedPush>(ConferenceDestroyedPush.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceDestroyedPush instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        }).register(new EventFormatterCallback<ConferenceEndedEvent>(ConferenceEndedEvent.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceEndedEvent instance) {
                map.putString("conferenceId", instance.getEvent().getConferenceId());
            }
        }).register(new EventFormatterCallback<ConferenceEnded>(ConferenceEnded.class) {
            @Override
            void transform(@NonNull WritableMap map, @NonNull ConferenceEnded instance) {
                map.putString("conferenceId", instance.getConferenceId());
            }
        });
    }

    public void onEvent(ConferenceCreatedError event) {
        emit(event);
    }

    public void onEvent(DeclineConferenceResultEvent event) {
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

    public void onEvent(StartVideoAnswerEvent event) {
        emit(event);
    }

    public void onEvent(StopVideoAnswerEvent event) {
        emit(event);
    }

    public void onEvent(PromiseParticipantAddedErrorEventException event) {
        emit(event);
    }

    public void onEvent(ConferenceCreationSuccess event) {
        emit(event);
    }

    public void onEvent(AddConferenceParticipantResultEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceRefreshedEvent event) {
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

    public void onEvent(StartRecordingResultEvent event) {
        emit(event);
    }

    public void onEvent(StopRecordingResultEvent event) {
        emit(event);
    }

    public void onEvent(GetConferenceStatusEvent event) {
        emit(event);
    }

    public void onEvent(GetConferenceStatusErrorEvent event) {
        emit(event);
    }

    public void onEvent(GetConferenceHistoryEvent event) {
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

    public void onEvent(SubscribeConferenceEvent event) {
        emit(event);
    }

    public void onEvent(SubscribeConferenceErrorEvent event) {
        emit(event);
    }

    public void onEvent(UnSubscribeConferenceAnswerEvent event) {
        emit(event);
    }

    public void onEvent(SubscribeForCallConferenceAnswerEvent event) {
        emit(event);
    }

    public void onEvent(SubscribeForCallConferenceErrorEvent event) {
        emit(event);
    }

    public void onEvent(UnSubscribeFromConferenceAnswerEvent event) {
        emit(event);
    }

    public void onEvent(UnsubscribeFromCallConferenceErrorEvent event) {
        emit(event);
    }

    public void onEvent(ConferencePreJoinedEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceJoinedError event) {
        emit(event);
    }

    public void onEvent(ConferenceJoinedSuccessEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceUsersInvitedEvent event) {
        emit(event);
    }

    public void onEvent(SendBroadcastResultEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceLeftSuccessEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceLeftError event) {
        emit(event);
    }

    public void onEvent(QualityIndicators event) {
        emit(event);
    }

    public void onEvent(ConferenceStatsEvent event) {
        emit(event);
    }

    public void onEvent(IncomingCallEvent event) {
        emit(event);
    }

    public void onEvent(RecordingStatusUpdateEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceUserCallDeclinedEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceDestroyedPushEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceDestroyedPush event) {
        emit(event);
    }

    public void onEvent(ConferenceEndedEvent event) {
        emit(event);
    }

    public void onEvent(ConferenceEnded event) {
        emit(event);
    }
}
