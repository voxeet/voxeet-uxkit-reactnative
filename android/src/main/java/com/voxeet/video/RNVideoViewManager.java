package com.voxeet.video;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.voxeet.VoxeetSDK;
import com.voxeet.android.media.MediaStream;
import com.voxeet.sdk.models.Participant;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.views.VideoView;

import java.util.List;
import java.util.Map;

public class RNVideoViewManager extends SimpleViewManager<VideoView> {
    public static final int IS_ATTACHED = 1;
    public static final int IS_SCREENSHARE = 2;

    public static final String PEER_ID = "peerId";
    public static final String LABEL = "label";
    public static final String STREAM_TYPE = "type";

    private static final String SCALE_FIT = "fit";
    private static final String SCALE_FILL = "fill";
    private static final String SCALE_BALANCED = "balanced";

    private static final String REACT_CLASS = "RCTVoxeetVideoView";
    private static final String TAG = RNVideoViewManager.class.getSimpleName();
    private EventDispatcher eventDispatcher;

    @NonNull
    @Override
    public String getName() {
        return RNVideoViewManager.REACT_CLASS;
    }

    @NonNull
    @Override
    protected VideoView createViewInstance(@NonNull ThemedReactContext reactContext) {

        if (null == eventDispatcher)
            eventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        return new VideoView(reactContext);
    }

    @ReactProp(name = "cornerRadius", defaultFloat = 0f)
    public void setCornerRadius(@NonNull VideoView view,
                                float cornerRadius) {
        view.setCornerRadius(cornerRadius);
    }

    @ReactProp(name = "isCircle", defaultBoolean = false)
    public void isCircle(@NonNull VideoView view,
                         boolean isCircle) {
        view.setIsCircle(isCircle);
    }

    @ReactProp(name = "hasFlip", defaultBoolean = false)
    public void hasFlip(@NonNull VideoView view,
                        boolean hasFlip) {
        view.setFlip(hasFlip);
    }

    @ReactProp(name = "attach")
    public void attach(@NonNull VideoView view,
                       @Nullable ReadableMap map) {
        ConferenceService service = VoxeetSDK.conference();
        if (null == service) {
            Log.d(TAG, "VideoView :: SDK NOT INITIALIZED");
        }

        if (null != map && map.hasKey(PEER_ID) && map.hasKey(LABEL)) {
            String peerId = map.getString(PEER_ID);
            String label = map.getString(LABEL);

            if (null == peerId) peerId = "";
            if (null == label) label = "";

            MediaStream mediaStream = tryToFindMediaStream(peerId, label);

            if (null != mediaStream) view.attach(peerId, mediaStream);
        } else {
            view.unAttach();
        }
    }

    @Nullable
    private MediaStream tryToFindMediaStream(String peerId, String label) {
        ConferenceService conferenceService = VoxeetSDK.conference();
        if (null == conferenceService) return null;

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
    public void scaleType(@NonNull VideoView view,
                          @Nullable String scaleType) {
        view.setVideoFill();
        if (null != scaleType) {
            switch (scaleType) {
                case SCALE_BALANCED:
                    view.setVideoBalanced();
                    break;
                case SCALE_FIT:
                    view.setVideoFit();
                    break;
                case SCALE_FILL:
                    view.setVideoFill();
                default:
            }
        }
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "isAttached", IS_ATTACHED,
                "isScreenShare", IS_SCREENSHARE
        );
    }

    @Override
    public void receiveCommand(VideoView view, int commandId, @Nullable ReadableArray args) {
        // This will be called whenever a command is sent from react-native.
        switch (commandId) {
            case IS_ATTACHED:
                boolean attached = view.isAttached();
                eventDispatcher.dispatchEvent(new RCTVideoViewBooleanEvent(args.getInt(0), attached));
                break;
            case IS_SCREENSHARE:
                boolean screenshare = view.isScreenShare();
                eventDispatcher.dispatchEvent(new RCTVideoViewBooleanEvent(args.getInt(0), screenshare));
        }
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(RCTVideoViewBooleanEvent.EVENT_NAME, MapBuilder.of("registrationName", "onCallReturn"));
    }
}
