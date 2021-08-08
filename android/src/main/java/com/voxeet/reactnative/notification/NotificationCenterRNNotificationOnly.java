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
        VoxeetLog.log(TAG, "onInvitation received, will set incomingInvitation+!accepted");
        PendingInvitationResolution.incomingInvitation = invitationBundle;
        PendingInvitationResolution.accepted = false;

        Intent serviceIntent = new Intent(context, RNVoxeetFirebaseIncomingNotificationService.class);
        serviceIntent.putExtras(invitationBundle.asBundle());
        context.startService(serviceIntent);
    }

    @Override
    public void onInvitationCanceled(@NonNull Context context, @NonNull String conferenceId) {
        Intent serviceIntent = new Intent(context, RNVoxeetFirebaseIncomingNotificationService.class);
        context.stopService(serviceIntent);
    }
}