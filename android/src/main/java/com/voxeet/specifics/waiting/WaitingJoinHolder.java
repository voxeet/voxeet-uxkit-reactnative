package com.voxeet.specifics.waiting;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.voxeet.RNVoxeetConferencekitModule;

/**
 * Created by kevinleperf on 20/12/2018.
 */

public class WaitingJoinHolder extends WaitingAbstractHolder {
    private String conferenceId;

    public WaitingJoinHolder(RNVoxeetConferencekitModule module, String conferenceId, Promise promise) {
        super(module, promise);
        this.conferenceId = conferenceId;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    @Override
    public void rejoin() {
        Log.d("WaitingJoinHolder", "rejoin: conferenceId:=" + conferenceId);
        module.join(conferenceId, promise);
        module = null;
        promise = null;
    }
}
