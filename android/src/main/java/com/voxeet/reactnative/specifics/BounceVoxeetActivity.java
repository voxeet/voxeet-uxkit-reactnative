package com.voxeet.reactnative.specifics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.voxeet.sdk.push.center.management.Constants;
import com.voxeet.uxkit.activities.VoxeetAppCompatActivity;

public class BounceVoxeetActivity extends VoxeetAppCompatActivity {

    private static Class<? extends Activity> ActivityClass;

    public static void registerBouncedActivity(Class<? extends Activity> activityClass) {
        ActivityClass = activityClass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<? extends Activity> activity = ActivityClass;
        if (null == activity) {
            try {
                activity = (Class<? extends Activity>) Class.forName(getPackageName() + ".MainActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        Intent intent = new Intent(this, activity);

        //- - - - - - - - - - - - - - - - - - - -
        //code from the VoxeetAppCompatActivity
        //- - - - - - - - - - - - - - - - - - - -

        intent.putExtra(Constants.CONF_ID, getExtraVoxeetBundleChecker().getConferenceId())
                .putExtra(Constants.INVITER_NAME, getExtraVoxeetBundleChecker().getUserName())
                .putExtra(Constants.INVITER_ID, getExtraVoxeetBundleChecker().getExternalUserId())
                .putExtra(Constants.INVITER_EXTERNAL_ID, getExtraVoxeetBundleChecker().getExternalUserId())
                .putExtra(Constants.INVITER_URL, getExtraVoxeetBundleChecker().getAvatarUrl());

        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}
