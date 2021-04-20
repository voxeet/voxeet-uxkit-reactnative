package com.voxeet.reactnative.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.voxeet.reactnative.specifics.RNVoxeetActivity;
import com.voxeet.reactnative.specifics.RNVoxeetActivityObject;
import com.voxeet.reactnative.utils.IntentUtils;
import com.voxeet.reactnative.utils.VoxeetLog;
import com.voxeet.sdk.push.center.invitation.IIncomingInvitationListener;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;

public class NotificationCenterRNNotificationOnly implements IIncomingInvitationListener {

    private static final String TAG = NotificationCenterRNNotificationOnly.class.getSimpleName();

    public NotificationCenterRNNotificationOnly() {

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onInvitation(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        RNVoxeetActivity instance = RNVoxeetActivityObject.getActivity();

        Intent activity = IntentUtils.createIntent(context, invitationBundle);
        if (null == activity) return;

        if (instance == null) {
            VoxeetLog.log(TAG, "onInvitation: startActivity " + activity);
            context.startActivity(activity);
        } else {
            VoxeetLog.log(TAG, "onInvitation: onInvitationBundle " + activity);
            instance.onInvitationBundle(activity);
        }
    }

    @Override
    public void onInvitationCanceled(@NonNull Context context, @NonNull String conferenceId) {

    }
}