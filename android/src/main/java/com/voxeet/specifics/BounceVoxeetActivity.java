package com.voxeet.specifics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import sdk.voxeet.com.toolkit.activities.workflow.VoxeetAppCompatActivity;
import voxeet.com.sdk.factories.VoxeetIntentFactory;

/**
 * Created by kevinleperf on 11/09/2018.
 */

public class BounceVoxeetActivity extends VoxeetAppCompatActivity {

    private static Class<? extends Activity> ActivityClass;

    public static void registerBouncedActivity(Class<? extends Activity> activityClass) {
        ActivityClass = activityClass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ActivityClass);

        //- - - - - - - - - - - - - - - - - - - -
        //code from the VoxeetAppCompatActivity
        //- - - - - - - - - - - - - - - - - - - -

        intent.putExtra(VoxeetIntentFactory.CONF_ID, getExtraVoxeetBundleChecker().getConferenceId())
                .putExtra(VoxeetIntentFactory.INVITER_NAME, getExtraVoxeetBundleChecker().getUserName())
                .putExtra(VoxeetIntentFactory.INVITER_ID, getExtraVoxeetBundleChecker().getExternalUserId())
                .putExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID, getExtraVoxeetBundleChecker().getExternalUserId())
                .putExtra(VoxeetIntentFactory.INVITER_URL, getExtraVoxeetBundleChecker().getAvatarUrl());

        //deprecated
        intent.putExtra("join", true);
        intent.putExtra("callMode", 0x0001);

        //TODO check usefullness
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        startActivity(intent);

        finish();
    }
}
