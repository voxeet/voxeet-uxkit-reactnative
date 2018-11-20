package com.voxeet.specifics;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.voxeet.toolkit.activities.notification.IncomingBundleChecker;
import com.voxeet.toolkit.activities.notification.IncomingCallFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.services.ScreenShareService;
import voxeet.com.sdk.events.error.ConferenceJoinedError;
import voxeet.com.sdk.events.error.PermissionRefusedEvent;
import voxeet.com.sdk.events.success.ConferenceJoinedSuccessEvent;
import voxeet.com.sdk.events.success.ConferencePreJoinedEvent;
import voxeet.com.sdk.utils.Validate;

/**
 * Created by kevinleperf on 11/09/2018.
 */

public abstract class RNVoxeetActivity extends ReactActivity {

    private RNVoxeetActivityObject mActivityObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityObject = new RNVoxeetActivityObject();
        mActivityObject.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mActivityObject.onResume(this);
    }

    @Override
    protected void onPause() {
        mActivityObject.onPause(this);

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mActivityObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mActivityObject.onNewIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mActivityObject.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
