package com.voxeet.reactnative.specifics;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.VoxeetSDK;
import com.voxeet.reactnative.notification.PendingInvitationResolution;
import com.voxeet.reactnative.utils.VoxeetLog;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.ScreenShareService;
import com.voxeet.sdk.services.screenshare.RequestScreenSharePermissionEvent;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.uxkit.activities.notification.IncomingBundleChecker;
import com.voxeet.uxkit.incoming.factory.IncomingCallFactory;

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

    private static final String TAG = RNVoxeetActivityObject.class.getSimpleName();
    private static RNVoxeetActivity mActivity;
    private boolean paused = false;

    @Nullable
    public static RNVoxeetActivity getActivity() {
        return mActivity;
    }

    public void onCreate(@NonNull RNVoxeetActivity activity) {

        mActivity = activity;

        BounceVoxeetActivity.registerBouncedActivity(activity.getClass());

        paused = false;
        onNewIntent(mActivity.getBaseContext(), mActivity.getIntent(), true);
    }

    public void onResume(@NonNull RNVoxeetActivity activity) {
        paused = false;
        VoxeetSDK.instance().register(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); //registering this activity
        }

        if (canBeRegisteredToReceiveCalls()) {
            IncomingCallFactory.setTempAcceptedIncomingActivity(BounceVoxeetActivity.class);
            IncomingCallFactory.setTempExtras(activity.getIntent().getExtras());
        }

        VoxeetSDK.screenShare().consumeRightsToScreenShare();
    }

    public void onPause(@NonNull RNVoxeetActivity activity) {
        paused = true;
        ConferenceService conferenceService = VoxeetSDK.conference();
        //stop fetching stats if any pending
        if (VoxeetSDK.instance().isInitialized() && !conferenceService.isLive()) {
            VoxeetSDK.localStats().stopAutoFetch();
        }

        //if (mActivity == activity) mActivity = null;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onDestroy() {
        mActivity = null;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRefusedEvent.RESULT_CAMERA: {
                ConferenceService conferenceService = VoxeetSDK.conference();
                if (conferenceService.isLive()) {
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

    public void onNewIntent(Context context, Intent intent, boolean creation) {
        onDirectIntent(context, intent, creation);
    }

    public void onDirectIntent(Context context, Intent intent, boolean creation) {
        IncomingBundleChecker checker = new IncomingBundleChecker(intent, null);

        VoxeetLog.log(TAG, "onNewIntent: checker.isBundleValid() " + checker.isBundleValid() + " creation//" + creation + " paused//" + paused + " pending//" + PendingInvitationResolution.incomingInvitation);
        if (checker.isBundleValid()) {
            if (PendingInvitationResolution.incomingInvitation == null) {
                InvitationBundle bundle = new InvitationBundle(intent.getExtras());
                PendingInvitationResolution.onIncomingInvitation(mActivity, bundle);
                PendingInvitationResolution.onForwardToIncomingCall(context, mActivity);
                if (creation) mActivity.moveTaskToBack(true);

                Intent new_intent = new Intent();
                new_intent.setAction(null);
                mActivity.setIntent(new_intent);
            } else if (PendingInvitationResolution.accepted) {
                if (!VoxeetSDK.session().isSocketOpen()) {
                    return;
                }

                if (VoxeetSDK.instance().isInitialized()) {
                    checker.onAccept();
                }
            } else if (PendingInvitationResolution.incomingInvitation != null) {

            }
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        ScreenShareService screenShareService = VoxeetSDK.screenShare();
        boolean managed = screenShareService.onActivityResult(requestCode, resultCode, data);

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
                PendingInvitationResolution.accepted = true;
                PendingInvitationResolution.incomingInvitation = null;
                break;
            default:
        }
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
