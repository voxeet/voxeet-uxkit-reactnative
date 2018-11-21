package com.voxeet.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import voxeet.com.sdk.json.UserInfo;
import voxeet.com.sdk.models.SdkParticipant;
import voxeet.com.sdk.models.impl.DefaultConferenceUser;

/**
 * Created by kevinleperf on 11/11/2018.
 */

public final class ConferenceUserUtil {
    private ConferenceUserUtil() {

    }

    @NonNull
    public static WritableMap toMap(@Nullable DefaultConferenceUser user) {

        WritableMap map = new WritableNativeMap();

        if(null != user) {
            UserInfo userInfo = user.getUserInfo();
            map.putString("userId", user.getUserId());
            map.putString("conferenceStatus", user.getConferenceStatus().name());

            if (null != user.getUserInfo()) {
                map.putString("name", userInfo.getName());
                map.putString("externalId", userInfo.getExternalId());
                map.putString("avatarUrl", userInfo.getAvatarUrl());
            }
        }

        return map;
    }

    public static WritableArray toMap(Iterable<DefaultConferenceUser> conferenceUsers) {
        WritableNativeArray array = new WritableNativeArray();
        if(null != conferenceUsers) {
            for (DefaultConferenceUser user: conferenceUsers) {
                array.pushMap(toMap(user));
            }
        }
        return array;
    }

    public static WritableMap toMap(SdkParticipant user) {
        WritableMap map = new WritableNativeMap();
        map.putString("userId", user.getUserId());
        map.putString("conferenceStatus", user.getStatus());

        if (null != user.getMetadata()) {
            map.putString("name", user.getMetadata().getExternalName());
            map.putString("externalId", user.getMetadata().getExternalId());
            map.putString("avatarUrl", user.getMetadata().getExternalPhotoUrl());
        }

        return map;
    }
}
