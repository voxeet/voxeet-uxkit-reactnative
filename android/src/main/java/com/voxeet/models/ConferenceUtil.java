package com.voxeet.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.voxeet.sdk.models.ConferenceResponse;
import com.voxeet.sdk.models.HistoryConference;
import com.voxeet.sdk.models.MetaData;
import com.voxeet.sdk.models.SdkParticipant;
import com.voxeet.sdk.models.SubscribeConference;
import com.voxeet.sdk.models.abs.Conference;
import com.voxeet.sdk.models.abs.ConferenceUser;

import java.util.List;

public final class ConferenceUtil {
    private ConferenceUtil() {

    }

    @NonNull
    public static WritableMap toMap(@Nullable ConferenceResponse response) {
        WritableMap map = new WritableNativeMap();
        if(null != response) {
            map.putString("conferenceId", response.getConfId());
            map.putString("conferenceAlias", response.getConfAlias());
            map.putBoolean("isNew", response.isNew());
        }
        return map;
    }

    @NonNull
    public static WritableMap toMap(@NonNull Conference conference) {
        WritableMap map = new WritableNativeMap();
        map.putString("conferenceId", conference.getConferenceId());
        map.putString("conferenceAlias", conference.getConferenceAlias());
        map.putString("conferenceType", conference.getConferenceType());

        WritableNativeArray array = new WritableNativeArray();
        List<ConferenceUser> users = conference.getConferenceUsers();
        for (ConferenceUser user : users) {
            array.pushMap(ConferenceUserUtil.toMap(user));
        }
        map.putArray("conferenceUsers", array);

        return map;
    }

    public static WritableMap toMap(SubscribeConference conference) {
        WritableMap map = new WritableNativeMap();
        map.putString("conferenceId", conference.getConferenceId());
        map.putString("conferenceAlias", conference.getConferenceAlias());
        map.putString("conferenceType", conference.getType());

        WritableNativeArray array = new WritableNativeArray();
        List<SdkParticipant> users = conference.getParticipants();
        for (SdkParticipant user : users) {
            array.pushMap(ConferenceUserUtil.toMap(user));
        }
        map.putArray("conferenceUsers", array);

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
        map.putString("conferenceId", conference.getConferenceId());
        map.putString("conferenceAlias", conference.getConferenceAlias());
        map.putString("conferenceType", conference.getConferenceType());
        map.putString("ownerId", conference.getOwnerId());
        map.putString("userId", conference.getUserId());
        map.putDouble("conferenceDuration", conference.getConferenceDuration());
        map.putDouble("recordingDuration", conference.getConferenceRecordingDuration());
        map.putDouble("conferenceTimestamp", conference.getConferenceTimestamp());
        map.putMap("metadata", toMap(conference.getMetadata()));

        return map;
    }

    private static WritableMap toMap(MetaData metadata) {
        WritableMap map = new WritableNativeMap();
        map.putString("name", metadata.getExternalName());
        map.putString("avatarUrl", metadata.getExternalPhotoUrl());
        map.putString("externalId", metadata.getExternalId());

        return map;
    }
}
