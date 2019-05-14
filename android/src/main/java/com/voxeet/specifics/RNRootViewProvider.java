package com.voxeet.specifics;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.voxeet.notification.RNIncomingBundleChecker;
import com.voxeet.notification.RNIncomingCallActivity;
import com.voxeet.sdk.core.VoxeetSdk;
import com.voxeet.sdk.events.error.ConferenceJoinedError;
import com.voxeet.sdk.events.success.ConferenceDestroyedPushEvent;
import com.voxeet.sdk.events.success.ConferenceJoinedSuccessEvent;
import com.voxeet.sdk.events.success.ConferencePreJoinedEvent;
import com.voxeet.toolkit.controllers.VoxeetToolkit;
import com.voxeet.toolkit.providers.rootview.DefaultRootViewProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RNRootViewProvider extends DefaultRootViewProvider {
    private final Application mApplication;
    private RNIncomingBundleChecker mRNIncomingBundleChecker;

    /**
     * @param application a valid application which be called to obtain events
     * @param toolkit
     */
    public RNRootViewProvider(@NonNull Application application, @NonNull VoxeetToolkit toolkit) {
        super(application, toolkit);

        mApplication = application;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        super.onActivityCreated(activity, bundle);

        if (!RNIncomingCallActivity.class.equals(activity.getClass())) {
            mRNIncomingBundleChecker = new RNIncomingBundleChecker(mApplication, activity.getIntent(), null);
            mRNIncomingBundleChecker.createActivityAccepted(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        super.onActivityStarted(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);

        if (!RNIncomingCallActivity.class.equals(activity.getClass())) {

            if (null != VoxeetSdk.getInstance() && !EventBus.getDefault().isRegistered(this)) {
                VoxeetSdk.getInstance().register(mApplication, this);
            }

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this); //registering this activity
            }

            RNIncomingBundleChecker checker = RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE;
            if (null != checker && checker.isBundleValid()) {
                if (null != VoxeetSdk.getInstance() && VoxeetSdk.getInstance().isSocketOpen()) {
                    checker.onAccept();
                    RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE = null;
                }
            }
            //TODO next steps, fix this call here
            /*mRNIncomingBundleChecker = new CordovaIncomingBundleChecker(mApplication, activity.getIntent(), null);

            if (mRNIncomingBundleChecker.isBundleValid()) {
                mRNIncomingBundleChecker.onAccept();
            }*/
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        super.onActivityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        super.onActivitySaveInstanceState(activity, bundle);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPushEvent event) {
        if (mRNIncomingBundleChecker != null)
            mRNIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferencePreJoinedEvent event) {
        if (mRNIncomingBundleChecker != null)
            mRNIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceJoinedSuccessEvent event) {
        if (mRNIncomingBundleChecker != null)
            mRNIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceJoinedError event) {
        if (mRNIncomingBundleChecker != null)
            mRNIncomingBundleChecker.flushIntent();
    }

}
