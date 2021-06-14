package com.voxeet.reactnative.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.voxeet.android.media.MediaStream;
import com.voxeet.reactnative.video.RNVideoViewManager;

public final class MediaStreamUtil {
    private MediaStreamUtil() {

    }

    @NonNull
    public static WritableArray toMap(@Nullable Iterable<MediaStream> streams) {
        WritableNativeArray array = new WritableNativeArray();
        if (null != streams) {
            for (MediaStream stream : streams) {
                array.pushMap(toMap(stream));
            }
        }
        return array;
    }

    @NonNull
    public static WritableMap toMap(@NonNull MediaStream stream) {
        WritableMap map = new WritableNativeMap();
        map.putString(RNVideoViewManager.PEER_ID, stream.peerId());
        map.putString(RNVideoViewManager.LABEL, stream.label());
        map.putString(RNVideoViewManager.STREAM_TYPE, stream.getType().name());
        map.putBoolean(RNVideoViewManager.HAS_AUDIO_TRACKS, stream.audioTracks().size() > 0);
        map.putBoolean(RNVideoViewManager.HAS_VIDEO_TRACKS, stream.videoTracks().size() > 0);
        return map;
    }
}
