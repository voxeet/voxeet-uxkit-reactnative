package com.voxeet.specifics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.ReactActivity;
import com.voxeet.RNVoxeetConferencekitModule;
import com.voxeet.specifics.waiting.WaitingAbstractHolder;

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
