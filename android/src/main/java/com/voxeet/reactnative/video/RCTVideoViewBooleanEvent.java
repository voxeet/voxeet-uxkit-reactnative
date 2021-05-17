package com.voxeet.reactnative.video;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class RCTVideoViewBooleanEvent extends Event<RCTVideoViewBooleanEvent> {
    final static String EVENT_NAME = "videoViewEventReturned";
    private final WritableMap payload;
    private final int requestId;

    private boolean result;

    public RCTVideoViewBooleanEvent(int viewTag, int requestId, boolean result) {
        this.result = result;
        this.requestId = requestId;

        payload = Arguments.createMap();
        payload.putBoolean("result", result);
        payload.putInt("requestId", requestId);

        this.init(viewTag);
    }

    public boolean isTrue() {
        return result;
    }

    @Override
    public String getEventName() {
        return RCTVideoViewBooleanEvent.EVENT_NAME;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), payload);
    }
}
