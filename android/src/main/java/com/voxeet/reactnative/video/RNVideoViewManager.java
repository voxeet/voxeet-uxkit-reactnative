package com.voxeet.reactnative.video;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.voxeet.VoxeetSDK;
import com.voxeet.android.media.MediaStream;
import com.voxeet.reactnative.R;
import com.voxeet.sdk.models.Participant;
import com.voxeet.sdk.services.ConferenceService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

public class RNVideoViewManager extends SimpleViewManager<RNVideoViewWrapper> {
    public static final int IS_ATTACHED = 1;
    public static final int IS_SCREENSHARE = 2;
    public static final int ATTACH = 3;
    public static final int UNATTACH = 4;

    public static final String PEER_ID = "peerId";
    public static final String LABEL = "streamId";
    public static final String STREAM_TYPE = "type";
    public static final String HAS_VIDEO_TRACKS = "hasVideoTracks";
    public static final String HAS_AUDIO_TRACKS = "hasAudioTracks";

    public static final String SCALE_FIT = "fit";
    public static final String SCALE_FILL = "fill";
    public static final String SCALE_BALANCED = "balanced";

    private static final String REACT_CLASS = "RCTVoxeetVideoView";
    private static final String TAG = RNVideoViewManager.class.getSimpleName();
    private Handler handler = new Handler(Looper.getMainLooper());
    private String savedScaleType = null;

    @NonNull
    @Override
    public String getName() {
        return RNVideoViewManager.REACT_CLASS;
    }

    @NonNull
    @Override
    protected RNVideoViewWrapper createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new RNVideoViewWrapper(reactContext);
    }

    @ReactProp(name = "cornerRadius", defaultFloat = 0f)
    public void setCornerRadius(@NonNull RNVideoViewWrapper view,
                                float cornerRadius) {
        view.setCornerRadius(cornerRadius);
    }

    @ReactProp(name = "isCircle", defaultBoolean = false)
    public void isCircle(@NonNull RNVideoViewWrapper view,
                         boolean isCircle) {
        view.setIsCircle(isCircle);
    }

    @ReactProp(name = "hasFlip", defaultBoolean = false)
    public void hasFlip(@NonNull RNVideoViewWrapper view,
                        boolean hasFlip) {
        Log.d(TAG, "hasFlip: not usable");
        //view.setFlip(hasFlip);
    }

    @ReactProp(name = "attach")
    public void attach(@NonNull RNVideoViewWrapper view,
                       @Nullable ReadableMap map) {
        if (null != map && map.hasKey(PEER_ID) && map.hasKey(LABEL)) {
            attach(view, map.getString(PEER_ID), map.getString(LABEL));
        } else {
            view.unAttach();
        }
    }

    @Nullable
    private MediaStream tryToFindMediaStream(String peerId, String label) {
        ConferenceService conferenceService = VoxeetSDK.conference();

        Participant user = conferenceService.findParticipantById(peerId);
        if (null == user) return null;

        List<MediaStream> list = user.streams();

        for (MediaStream in_list : list) {
            if (null != in_list && peerId.equals(in_list.peerId()) && label.equals(in_list.label())) {
                return in_list;
            }
        }
        return null;
    }

    @ReactProp(name = "scaleType")
    public void scaleType(@NonNull RNVideoViewWrapper view,
                          @Nullable String scaleType) {
        view.scaleType(scaleType);
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "isAttached", IS_ATTACHED,
                "isScreenShare", IS_SCREENSHARE,
                "attach", ATTACH,
                "unattach", UNATTACH
        );
    }

    @Override
    public void receiveCommand(@NonNull RNVideoViewWrapper view, int commandId, @Nullable ReadableArray args) {
        handler.post(() -> receiveCommandOnMainThread(view, commandId, args));
    }

    public void receiveCommandOnMainThread(RNVideoViewWrapper view, int commandId, @Nullable ReadableArray args) {
        // This will be called whenever a command is sent from react-native.
        int requestId = null != args && !args.isNull(0) ? args.getInt(0) : -1;

        switch (commandId) {
            case IS_ATTACHED:
                boolean attached = view.isAttached();
                EventBus.getDefault().post(new RNVideoViewInternalEvent(requestId, attached));
                return;
            case IS_SCREENSHARE:
                boolean screenshare = view.isScreenShare();
                EventBus.getDefault().post(new RNVideoViewInternalEvent(requestId, screenshare));
                return;
            case ATTACH:
                try {
                    if (null != args && args.size() >= 3) {
                        String peerId = args.getString(1);
                        String labelId = args.getString(2);

                        attach(view, peerId, labelId);
                        EventBus.getDefault().post(new RNVideoViewInternalEvent(requestId, true));
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                EventBus.getDefault().post(new RNVideoViewInternalEvent(requestId, false));
                return;
            case UNATTACH:
                unattach(view);
                EventBus.getDefault().post(new RNVideoViewInternalEvent(requestId, true));
            default:
        }
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(RCTVideoViewBooleanEvent.EVENT_NAME, MapBuilder.of("registrationName", "onCallReturn"));
    }

    private void attach(@NonNull RNVideoViewWrapper view, @Nullable String peerId, @Nullable String label) {
        try {
            if (null == peerId) peerId = "";
            if (null == label) label = "";

            MediaStream mediaStream = tryToFindMediaStream(peerId, label);

            if (null != mediaStream) {
                view.attach(peerId, mediaStream);
            }
        } catch (Exception e) {
            Log.d(TAG, "attach: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void unattach(@Nullable RNVideoViewWrapper view) {
        try {
            Log.d(TAG, "unattach: " + view);
            if (null != view) view.unAttach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
