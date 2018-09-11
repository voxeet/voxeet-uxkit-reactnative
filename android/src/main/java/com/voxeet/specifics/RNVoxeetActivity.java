package com.voxeet.specifics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.react.ReactActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sdk.voxeet.com.toolkit.activities.notification.IncomingBundleChecker;
import sdk.voxeet.com.toolkit.activities.notification.IncomingCallFactory;
import sdk.voxeet.com.toolkit.activities.workflow.VoxeetAppCompatActivity;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.services.ScreenShareService;
import voxeet.com.sdk.events.error.ConferenceJoinedError;
import voxeet.com.sdk.events.success.ConferenceJoinedSuccessEvent;
import voxeet.com.sdk.events.success.ConferencePreJoinedEvent;

/**
 * Created by kevinleperf on 11/09/2018.
 */

public abstract class RNVoxeetActivity extends ReactActivity {


    private IncomingBundleChecker mIncomingBundleChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BounceVoxeetActivity.registerBouncedActivity(this.getClass());

        //create a check incoming call
        mIncomingBundleChecker = new IncomingBundleChecker(getIntent(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != VoxeetSdk.getInstance()) {
            VoxeetSdk.getInstance().register(this, this);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); //registering this activity
        }

        if (canBeRegisteredToReceiveCalls()) {
            IncomingCallFactory.setTempAcceptedIncomingActivity(BounceVoxeetActivity.class);
            IncomingCallFactory.setTempExtras(getIntent().getExtras());
        }

        if (mIncomingBundleChecker.isBundleValid()) {
            mIncomingBundleChecker.onAccept();
        }

        if(null != VoxeetSdk.getInstance()) {
            VoxeetSdk.getInstance().getScreenShareService().consumeRightsToScreenShare();
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mIncomingBundleChecker = new IncomingBundleChecker(intent, null);
        if (mIncomingBundleChecker.isBundleValid()) {
            mIncomingBundleChecker.onAccept();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean managed = false;
        if (null != VoxeetSdk.getInstance()) {
            managed = VoxeetSdk.getInstance().getScreenShareService().onActivityResult(requestCode, resultCode, data);
        }

        if (!managed) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ScreenShareService.RequestScreenSharePermissionEvent event) {
        VoxeetSdk.getInstance().getScreenShareService()
                .sendUserPermissionRequest(this);
    }

    //those two methods were here to manage sound type
    //no longer required
    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onEven(ConferencePreJoinedEvent event) {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPushEvent event) {
        mIncomingBundleChecker.flushIntent();

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }*/

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferencePreJoinedEvent event) {
        mIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceJoinedSuccessEvent event) {
        mIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceJoinedError event) {
        mIncomingBundleChecker.flushIntent();
    }

    /**
     * Get the current voxeet bundle checker
     * <p>
     * usefull to retrieve info about the notification (if such)
     * - user name
     * - avatar url
     * - conference id
     * - user id
     * - external user id
     * - extra bundle (custom)
     *
     * @return a nullable object
     */
    @Nullable
    protected IncomingBundleChecker getExtraVoxeetBundleChecker() {
        return mIncomingBundleChecker;
    }

    /**
     * Method called during the onResume of this
     *
     * @return true by default, override to change behaviour
     */
    protected boolean canBeRegisteredToReceiveCalls() {
        return true;
    }
}
