package com.voxeet.reactnative.notification;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.voxeet.sdk.push.center.invitation.IIncomingInvitationListener;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.utils.Opt;
import com.voxeet.uxkit.common.UXKitLogger;
import com.voxeet.uxkit.common.logging.ShortLogger;

public class NotificationCenterRNNotificationOnly implements IIncomingInvitationListener {

    private final ShortLogger Log = UXKitLogger.createLogger(NotificationCenterRNNotificationOnly.class);

    public NotificationCenterRNNotificationOnly() {

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onInvitation(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Log.d("onInvitation received, will set incomingInvitation+!accepted");
        if(null != PendingInvitationResolution.incomingInvitation) {
            String id = Opt.of(PendingInvitationResolution.incomingInvitation).then(i -> i.conferenceId)
                    .or("");

            if (id.equals(invitationBundle.conferenceId)) {
                Log.d("actually... this invitation is actually still pending, so it will wait");
                return;
            }
            try {
                throw new IllegalStateException("");
            } catch(Throwable t) {
                Log.e("debug", t);
            }
        }
        PendingInvitationResolution.incomingInvitation = invitationBundle;
        PendingInvitationResolution.accepted = false;

        RNVoxeetFirebaseIncomingNotificationService.start(context, invitationBundle);
    }

    @Override
    public void onInvitationCanceled(@NonNull Context context, @NonNull String conferenceId) {
        RNVoxeetFirebaseIncomingNotificationService.stop(context, conferenceId, null);
    }
}