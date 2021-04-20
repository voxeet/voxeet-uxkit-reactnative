package com.voxeet.reactnative.specifics;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.voxeet.VoxeetSDK;
import com.voxeet.reactnative.notification.RNIncomingBundleChecker;
import com.voxeet.reactnative.notification.RNIncomingCallActivity;
import com.voxeet.reactnative.utils.VoxeetLog;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.providers.rootview.DefaultRootViewProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RNRootViewProvider extends DefaultRootViewProvider {
    private final static String TAG = RNRootViewProvider.class.getSimpleName();

    private final Application mApplication;
    private RNIncomingBundleChecker mRNIncomingBundleChecker;

    /**
     * @param application a valid application which be called to obtain events
     * @param toolkit
     */
    public RNRootViewProvider(@NonNull Application application, @NonNull VoxeetToolkit toolkit) {
        super(application, toolkit);

        log("creating RNRootViewProvider");

        mApplication = application;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        super.onActivityCreated(activity, bundle);

        log("onActivityCreated :: creating checker");
        if (!RNIncomingCallActivity.class.equals(activity.getClass())) {
            mRNIncomingBundleChecker = new RNIncomingBundleChecker(mApplication, activity.getIntent(), null);
            log("onActivityCreated :: creating checker done");
        } else {
            log("onActivityCreated :: creating checker canceled");
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        log("onActivityStarted");
        super.onActivityStarted(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        log("onActivityResumed");
        super.onActivityResumed(activity);

        if (!RNIncomingCallActivity.class.equals(activity.getClass())) {
            log("onActivityResumed :: subscribe to events");
            VoxeetSDK.instance().register(this);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this); //registering this activity
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        log("onActivityPaused");
        super.onActivityPaused(activity);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        log("onActivityStopped");
        super.onActivityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        log("onActivitySaveInstanceState");
        super.onActivitySaveInstanceState(activity, bundle);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        log("onActivityDestroyed");
        super.onActivityDestroyed(activity);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        log("ConferenceStatusUpdatedEvent");
        switch (event.state) {
            case JOINING:
            case JOINED:
            case ERROR:
                if (mRNIncomingBundleChecker != null)
                    mRNIncomingBundleChecker.flushIntent();
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPush event) {
        log("ConferenceDestroyedPush");
        if (mRNIncomingBundleChecker != null)
            mRNIncomingBundleChecker.flushIntent();
    }

    private final void log(@NonNull String text) {
        VoxeetLog.log(TAG, "log: " + text);
    }
}
