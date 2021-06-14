package com.voxeet.reactnative.specifics.waiting;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.voxeet.reactnative.RNVoxeetConferencekitModule;

/**
 * Created by kevinleperf on 20/12/2018.
 */

public class WaitingJoinHolder extends WaitingAbstractHolder {
    private ReadableMap map;
    private String conferenceId;

    public WaitingJoinHolder(RNVoxeetConferencekitModule module, String conferenceId, ReadableMap map, Promise promise) {
        super(module, promise);
        this.conferenceId = conferenceId;
        this.map = map;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    @Override
    public void rejoin() {
        Log.d("WaitingJoinHolder", "rejoin: conferenceId:=" + conferenceId);
        module.join(conferenceId, map, promise);
        module = null;
        map = null;
        promise = null;
    }
}
