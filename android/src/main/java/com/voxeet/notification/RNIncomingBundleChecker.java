package com.voxeet.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.push.center.management.Constants;
import com.voxeet.uxkit.activities.notification.IncomingBundleChecker;

public class RNIncomingBundleChecker extends IncomingBundleChecker {

    private final static String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";

    private Context mContext;

    public RNIncomingBundleChecker(Context context, @NonNull Intent intent, @Nullable IExtraBundleFillerListener filler_listener) {
        super(intent, filler_listener);

        mContext = context;
    }

    final public boolean isSameConference(@Nullable Conference conference) {
        if (null == conference) return false;
        return isSameConference(conference.getId());
    }

    /**
     * Create an intent to start the activity you want after an "accept" call
     *
     * @param caller the non null caller
     * @return a valid intent
     */
    @SuppressLint("WrongConstant")
    @NonNull
    final public Intent createRNActivityAccepted(@NonNull Activity caller) {
        Class to_call = createClassToCall();

        //if call is disabled
        if (null == to_call) return null;

        Intent intent = new Intent(caller, to_call);

        //inject the extras from the current "loaded" activity
        Bundle extras = null;//here was the extras provider call
        if (null != extras) {
            intent.putExtras(extras);
        }

        intent.putExtra(BUNDLE_EXTRA_BUNDLE, createExtraBundle());

        intent.putExtra(Constants.CONF_ID, getConferenceId())
                .putExtra(Constants.INVITER_NAME, getUserName())
                .putExtra(Constants.INVITER_ID, getExternalUserId())
                .putExtra(Constants.INVITER_EXTERNAL_ID, getExternalUserId())
                .putExtra(Constants.INVITER_URL, getAvatarUrl());

        //deprecated
        intent.putExtra("join", true);
        intent.putExtra("callMode", 0x0001);

        //TODO check usefullness
        intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        return intent;
    }

    private Class createClassToCall() {
        try {
            Class klass = Class.forName(mContext.getPackageName() + ".MainActivity");
            return klass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
