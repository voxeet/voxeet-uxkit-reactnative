package com.voxeet.reactnative.video;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.android.media.MediaStream;
import com.voxeet.reactnative.R;
import com.voxeet.sdk.views.RoundedFrameLayout;
import com.voxeet.sdk.views.VideoView;

import static com.voxeet.reactnative.video.RNVideoViewManager.SCALE_BALANCED;
import static com.voxeet.reactnative.video.RNVideoViewManager.SCALE_FILL;
import static com.voxeet.reactnative.video.RNVideoViewManager.SCALE_FIT;

public class RNVideoViewWrapper extends RoundedFrameLayout {

    @Nullable
    private VideoView videoView = null;

    @Nullable
    private String scaleType = null;

    public RNVideoViewWrapper(@NonNull Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.wrapper, this, false);
        addView(view);
        videoView = view.findViewById(R.id.videoView);
    }

    public void unAttach() {
        if (null != videoView) {
            videoView.unAttach();
        }
    }

    public void setIsMirror(boolean isMirror) {
        if (null != videoView) {
            videoView.setMirror(isMirror);
        }
    }

    public void scaleType(String scaleType) {
        if (null == scaleType) scaleType = "";
        this.scaleType = scaleType;

        if (null == videoView) return;

        switch (scaleType) {
            case SCALE_BALANCED:
                videoView.setVideoBalanced();
                break;
            case SCALE_FIT:
                videoView.setVideoFit();
                break;
            default:
            case SCALE_FILL:
                videoView.setVideoFill();
        }
    }

    public boolean isAttached() {
        return null != videoView && videoView.isAttached();
    }

    public boolean isScreenShare() {
        return null != videoView && videoView.isScreenShare();
    }

    public void attach(String peerId, MediaStream mediaStream) {
        videoView.attach(peerId, mediaStream);
        requestLayout();
    }
}