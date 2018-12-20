package com.voxeet.specifics.waiting;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.voxeet.RNVoxeetConferencekitModule;

/**
 * Created by kevinleperf on 20/12/2018.
 */

public class WaitingStartConferenceHolder extends WaitingAbstractHolder {
    private ReadableArray array;
    private String conferenceAlias;

    public WaitingStartConferenceHolder(RNVoxeetConferencekitModule module,
                                        String conferenceAlias,
                                        ReadableArray array,
                                        Promise promise) {
        super(module, promise);
        this.conferenceAlias = conferenceAlias;
        this.array = array;
    }

    public String getConferenceAlias() {
        return conferenceAlias;
    }

    @Override
    public void rejoin() {
        Log.d("WaitingJoinHolder", "rejoin: conferenceAlias:=" + conferenceAlias);
        module.startConference(conferenceAlias, array, promise);
        module = null;
        promise = null;
    }
}
