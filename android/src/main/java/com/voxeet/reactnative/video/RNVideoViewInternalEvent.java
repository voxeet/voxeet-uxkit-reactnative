package com.voxeet.reactnative.video;

public class RNVideoViewInternalEvent {

    public final int requestId;
    public final boolean result;

    public RNVideoViewInternalEvent(int requestId, boolean result) {
        this.requestId = requestId;
        this.result = result;
    }
}
