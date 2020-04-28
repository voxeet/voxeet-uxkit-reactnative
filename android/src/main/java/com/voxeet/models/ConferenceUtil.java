package com.voxeet.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.models.Participant;
import com.voxeet.sdk.models.v1.CreateConferenceResult;
import com.voxeet.sdk.models.v1.HistoryConference;
import com.voxeet.sdk.models.v1.MetaData;
import com.voxeet.sdk.models.v1.SdkParticipant;
import com.voxeet.sdk.models.v1.SubscribeConference;

import java.util.List;

public final class ConferenceUtil {

    public static String CONFERENCE_ID = "conferenceId";
    public static String CONFERENCE_ALIAS = "conferenceAlias";
    public static String CONFERENCE_IS_NEW = "isNew";
    public static String CONFERENCE_PARTICIPANTS = "conferenceUsers";
    public static String CONFERENCE_TYPE = "conferenceType";
    public static String CONFERENCE_OWNER_ID = "ownerId";
    public static String CONFERENCE_DURATION = "conferenceDuration";
    public static String CONFERENCE_RECORDING_DURATION = "recordingDuration";
    public static String CONFERENCE_TIMESTAMP = "conferenceTimestamp";
    public static String CONFERENCE_METADATA = "metadata";

    private ConferenceUtil() {

    }

    @NonNull
    public static WritableMap toMap(@Nullable CreateConferenceResult response) {
        WritableMap map = new WritableNativeMap();
        if(null != response) {
            map.putString(CONFERENCE_ID, response.conferenceId);
            map.putString(CONFERENCE_ALIAS, response.conferenceAlias);
            map.putBoolean(CONFERENCE_IS_NEW, response.isNew);
        }
        return map;
    }

    @NonNull
    public static WritableMap toMap(@NonNull Conference conference) {
        WritableMap map = new WritableNativeMap();
        map.putString(CONFERENCE_ID, conference.getId());
        map.putString(CONFERENCE_ALIAS, conference.getAlias());

        WritableNativeArray array = new WritableNativeArray();
        List<Participant> users = conference.getParticipants();
        for (Participant user : users) {
            array.pushMap(ConferenceUserUtil.toMap(user));
        }
        map.putArray(CONFERENCE_PARTICIPANTS, array);

        return map;
    }

    public static WritableMap toMap(SubscribeConference conference) {
        WritableMap map = new WritableNativeMap();
        map.putString(CONFERENCE_ID, conference.getConferenceId());
        map.putString(CONFERENCE_ALIAS, conference.getConferenceAlias());
        map.putString(CONFERENCE_TYPE, conference.getType());

        WritableNativeArray array = new WritableNativeArray();
        List<SdkParticipant> users = conference.getParticipants();
        for (SdkParticipant user : users) {
            array.pushMap(ConferenceUserUtil.toMap(user));
        }
        map.putArray(CONFERENCE_PARTICIPANTS, array);

        return map;
    }

    public static WritableArray toMap(List<HistoryConference> items) {
        WritableNativeArray array = new WritableNativeArray();
        if (null != items) {
            for (HistoryConference item : items) {
                array.pushMap(ConferenceUtil.toMap(item));
            }
        }
        return array;
    }

    private static WritableMap toMap(HistoryConference conference) {
        WritableMap map = new WritableNativeMap();
        map.putString(CONFERENCE_ID, conference.getConferenceId());
        map.putString(CONFERENCE_ALIAS, conference.getConferenceAlias());
        map.putString(CONFERENCE_TYPE, conference.getConferenceType());
        map.putString(CONFERENCE_OWNER_ID, conference.getOwnerId());
        map.putString(ConferenceUserUtil.PARTICIPANT_ID, conference.getUserId());
        map.putDouble(CONFERENCE_DURATION, conference.getConferenceDuration());
        map.putDouble(CONFERENCE_RECORDING_DURATION, conference.getConferenceRecordingDuration());
        map.putDouble(CONFERENCE_TIMESTAMP, conference.getConferenceTimestamp());
        map.putMap(CONFERENCE_METADATA, toMap(conference.getMetadata()));

        return map;
    }

    private static WritableMap toMap(MetaData metadata) {
        WritableMap map = new WritableNativeMap();
        map.putString(ConferenceUserUtil.PARTICIPANT_NAME, metadata.getExternalName());
        map.putString(ConferenceUserUtil.PARTICIPANT_AVATAR_URL, metadata.getExternalPhotoUrl());
        map.putString(ConferenceUserUtil.PARTICIPANT_EXTERNAL_ID, metadata.getExternalId());

        return map;
    }
}
