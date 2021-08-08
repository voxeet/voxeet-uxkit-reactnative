package com.voxeet.reactnative.specifics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.react.ReactActivity;
import com.voxeet.reactnative.RNVoxeetConferencekitModule;
import com.voxeet.reactnative.specifics.waiting.WaitingAbstractHolder;

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
        RNVoxeetConferencekitModule.registerActivity(this);
    }

    @Override
    protected void onPause() {
        //no need to unregister, it's not having multiple activities
        mActivityObject.onPause(this);

        super.onPause();
    }

    public void onInvitationBundle(Intent intent) {
        mActivityObject.onDirectIntent(this.getBaseContext(), intent, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        WaitingAbstractHolder holder = RNVoxeetConferencekitModule.getWaitingJoinHolder();

        if (null != holder && RNVoxeetConferencekitModule.isWaiting()) {
            int i = 0;

            while (i < permissions.length && i < grantResults.length) {
                if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {
                    if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        holder.rejoin();
                    } else {
                        try {
                            throw new IllegalStateException("No mic permission granted, can't join");
                        } catch (Exception e) {
                            e.printStackTrace();
                            holder.getPromise().reject("NO_MIC_PERMISSION", e);
                        }
                    }
                    //managed
                    return;
                }
                i++;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mActivityObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        mActivityObject.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mActivityObject.onNewIntent(this.getBaseContext(), intent, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mActivityObject.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
