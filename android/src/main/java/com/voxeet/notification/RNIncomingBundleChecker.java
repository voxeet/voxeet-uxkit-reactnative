package com.voxeet.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.voxeet.toolkit.controllers.VoxeetToolkit;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.factories.VoxeetIntentFactory;
import voxeet.com.sdk.json.UserInfo;

public class RNIncomingBundleChecker {

    private final static String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";
    private Context mContext;

    @Nullable
    private IExtraBundleFillerListener mFillerListener;

    @NonNull
    private Intent mIntent;

    @Nullable
    private String mUserName;

    @Nullable
    private String mUserId;

    @Nullable
    private String mExternalUserId;

    @Nullable
    private String mAvatarUrl;

    @Nullable
    private String mConferenceId;

    private RNIncomingBundleChecker() {
        mIntent = new Intent();
    }

    public RNIncomingBundleChecker(Context context, @NonNull Intent intent, @Nullable IExtraBundleFillerListener filler_listener) {
        this();

        mContext = context;
        mFillerListener = filler_listener;
        mIntent = intent;

        if (null != mIntent) {
            mUserName = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_NAME);
            mExternalUserId = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID);
            mUserId = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_ID);
            mAvatarUrl = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_URL);
            mConferenceId = mIntent.getStringExtra(VoxeetIntentFactory.CONF_ID);
        }
    }

    /**
     * Call accepted invitation
     * <p>
     * this must be called from the activity launched
     * not from the incoming call activity (!)
     */
    public void onAccept() {
        if (mConferenceId != null) {
            UserInfo info = new UserInfo(getUserName(),
                    getExternalUserId(),
                    getAvatarUrl());

            VoxeetToolkit.getInstance()
                    .getConferenceToolkit()
                    .joinUsingConferenceId(mConferenceId, info)
                    .then(new PromiseExec<Boolean, Object>() {
                        @Override
                        public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                            //possible callback to set ?
                        }
                    })
                    .error(new ErrorPromise() {
                        @Override
                        public void onError(Throwable error) {
                            error.printStackTrace();
                        }
                    });
        }
    }

    /**
     * Check the current intent
     *
     * @return true if the intent has notification keys
     */
    final public boolean isBundleValid() {
        return null != mIntent
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_NAME)
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID)
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_ID)
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_URL)
                && mIntent.hasExtra(VoxeetIntentFactory.CONF_ID);
    }

    @Nullable
    final public String getExternalUserId() {
        return mExternalUserId;
    }

    @Nullable
    final public String getUserId() {
        return mUserId;
    }

    @Nullable
    final public String getUserName() {
        return mUserName;
    }

    @Nullable
    final public String getAvatarUrl() {
        return mAvatarUrl;
    }

    @Nullable
    final public String getConferenceId() {
        return mConferenceId;
    }

    @Nullable
    final public Bundle getExtraBundle() {
        return null != mIntent ? mIntent.getBundleExtra(BUNDLE_EXTRA_BUNDLE) : null;
    }

    final public boolean isSameConference(String conferenceId) {
        return mConferenceId != null && mConferenceId.equals(conferenceId);
    }


    /**
     * Create an intent to start the activity you want after an "accept" call
     *
     * @param caller the non null caller
     * @return a valid intent
     */
    @NonNull
    final public Intent createActivityAccepted(@NonNull Activity caller) {
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

        intent.putExtra(VoxeetIntentFactory.CONF_ID, getConferenceId())
                .putExtra(VoxeetIntentFactory.INVITER_NAME, getUserName())
                .putExtra(VoxeetIntentFactory.INVITER_ID, getExternalUserId())
                .putExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID, getExternalUserId())
                .putExtra(VoxeetIntentFactory.INVITER_URL, getAvatarUrl());

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

    /**
     * Remove the specific bundle call keys from the intent
     * Needed if you do not want to pass over and over in this method
     * in onResume/onPause lifecycle
     */
    public void flushIntent() {
        if (null != mIntent) {
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_ID);
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID);
            mIntent.removeExtra(VoxeetIntentFactory.CONF_ID);
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_URL);
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_NAME);
        }
    }

    @NonNull
    public Bundle createExtraBundle() {
        Bundle extra = null;

        if (null != mFillerListener)
            extra = mFillerListener.createExtraBundle();

        if (null == extra) extra = new Bundle();
        return extra;
    }

    public static interface IExtraBundleFillerListener {

        @Nullable
        Bundle createExtraBundle();
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
