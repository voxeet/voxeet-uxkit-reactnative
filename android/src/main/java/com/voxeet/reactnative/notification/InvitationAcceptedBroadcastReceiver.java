package com.voxeet.reactnative.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voxeet.sdk.push.center.invitation.InvitationBundle;

public class InvitationAcceptedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //override the incomingInvitation set because it could be in context of multiple invitations
        PendingInvitationResolution.incomingInvitation = new InvitationBundle(intent.getExtras());
        PendingInvitationResolution.onIncomingInvitationAccepted(context);
        context.stopService(new Intent(context, RNVoxeetFirebaseIncomingNotificationService.class));
    }

}