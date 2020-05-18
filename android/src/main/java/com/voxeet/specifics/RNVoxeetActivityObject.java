package com.voxeet.specifics;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.VoxeetSDK;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.ScreenShareService;
import com.voxeet.sdk.services.screenshare.RequestScreenSharePermissionEvent;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.toolkit.activities.notification.IncomingBundleChecker;
import com.voxeet.toolkit.incoming.factory.IncomingCallFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Class managing the communication between the Activity and the underlying Bundle manager
 * <p>
 * To integrate this, if your MainActivity extends ReactActivity, simply replace ReactActivity with
 * RNVoxeetActivity
 * <p>
 * if your app extends an other non-ReactNative application, please bind the methods used in the RNVoxeetActivity
 */

public class RNVoxeetActivityObject {

    private IncomingBundleChecker mIncomingBundleChecker;
    private Activity mActivity;

    public void onCreate(@NonNull Activity activity) {

        mActivity = activity;

        BounceVoxeetActivity.registerBouncedActivity(activity.getClass());

        mIncomingBundleChecker = new IncomingBundleChecker(activity.getIntent(), null);
    }

    public void onResume(@NonNull Activity activity) {
        if (null != VoxeetSDK.instance()) {
            VoxeetSDK.instance().register(this);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); //registering this activity
        }

        if (canBeRegisteredToReceiveCalls()) {
            IncomingCallFactory.setTempAcceptedIncomingActivity(BounceVoxeetActivity.class);
            IncomingCallFactory.setTempExtras(activity.getIntent().getExtras());
        }

        if (mIncomingBundleChecker.isBundleValid()) {
            if (null != VoxeetSDK.instance()) {
                mIncomingBundleChecker.onAccept();
            } else {
                //RNVoxeetConferencekitModule.AWAITING_OBJECT = this;
            }
        }

        ScreenShareService screenShareService = VoxeetSDK.screenShare();
        if (null != screenShareService) {
            screenShareService.consumeRightsToScreenShare();
        }
    }

    @Nullable
    public IncomingBundleChecker getIncomingBundleChecker() {
        return mIncomingBundleChecker;
    }

    public void onPause(@NonNull Activity activity) {
        ConferenceService conferenceService = VoxeetSDK.conference();
        if (null != conferenceService) {
            //stop fetching stats if any pending
            if (!conferenceService.isLive()) {
                VoxeetSDK.localStats().stopAutoFetch();
            }
        }
        if (mActivity == activity) mActivity = null;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRefusedEvent.RESULT_CAMERA: {
                ConferenceService conferenceService = VoxeetSDK.conference();
                if (null != conferenceService && conferenceService.isLive()) {
                    VoxeetSDK.conference().startVideo()
                            .then(result -> {

                            })
                            .error(Throwable::printStackTrace);
                }
                return;
            }
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionRefusedEvent event) {
        if (null != event.getPermission()) {
            switch (event.getPermission()) {
                case CAMERA:
                    Validate.requestMandatoryPermissions(mActivity,
                            new String[]{Manifest.permission.CAMERA},
                            PermissionRefusedEvent.RESULT_CAMERA);
                    break;
            }
        }
    }

    public void onNewIntent(Intent intent) {
        mIncomingBundleChecker = new IncomingBundleChecker(intent, null);
        if (mIncomingBundleChecker.isBundleValid()) {
            if (null != VoxeetSDK.instance()) {
                mIncomingBundleChecker.onAccept();
            } else {
                //RNVoxeetConferencekitModule.AWAITING_OBJECT = this;
            }
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean managed = false;
        ScreenShareService screenShareService = VoxeetSDK.screenShare();
        if (null != screenShareService) {
            managed = screenShareService.onActivityResult(requestCode, resultCode, data);
        }

        return managed;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RequestScreenSharePermissionEvent event) {
        VoxeetSDK.screenShare().sendUserPermissionRequest(mActivity);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        switch (event.state) {
            case JOINING:
            case JOINED:
            case ERROR:
                mIncomingBundleChecker.flushIntent();
                break;
            default:
        }
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
