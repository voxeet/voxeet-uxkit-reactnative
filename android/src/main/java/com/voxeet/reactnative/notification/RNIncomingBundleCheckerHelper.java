package com.voxeet.reactnative.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.voxeet.sdk.push.center.management.Constants;
import com.voxeet.uxkit.common.activity.bundle.IncomingBundleChecker;

public class RNIncomingBundleCheckerHelper {

    private final static String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";

    /**
     * Create an intent to start the activity you want after an "accept" call
     *
     * @param caller the non null caller
     * @return a valid intent
     */
    @SuppressLint("WrongConstant")
    @NonNull
    final public static Intent createRNActivityAccepted(@NonNull Context context,
                                                        @NonNull Activity caller,
                                                        @NonNull IncomingBundleChecker bundleChecker) {
        Class to_call = createClassToCall(context);

        //if call is disabled
        if (null == to_call) return null;

        Intent intent = new Intent(caller, to_call);

        //inject the extras from the current "loaded" activity
        Bundle extras = null;//here was the extras provider call
        if (null != extras) {
            intent.putExtras(extras);
        }

        intent.putExtra(BUNDLE_EXTRA_BUNDLE, bundleChecker.createExtraBundle());

        intent.putExtra(Constants.CONF_ID, bundleChecker.getConferenceId())
                .putExtra(Constants.INVITER_NAME, bundleChecker.getUserName())
                .putExtra(Constants.INVITER_ID, bundleChecker.getExternalUserId())
                .putExtra(Constants.INVITER_EXTERNAL_ID, bundleChecker.getExternalUserId())
                .putExtra(Constants.INVITER_URL, bundleChecker.getAvatarUrl());

        intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    private static Class createClassToCall(@NonNull Context context) {
        try {
            Class klass = Class.forName(context.getPackageName() + ".MainActivity");
            return klass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
