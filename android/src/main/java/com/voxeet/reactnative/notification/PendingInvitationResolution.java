package com.voxeet.reactnative.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.voxeet.reactnative.specifics.RNVoxeetActivity;
import com.voxeet.reactnative.specifics.RNVoxeetActivityObject;
import com.voxeet.reactnative.utils.IntentUtils;
import com.voxeet.reactnative.utils.VoxeetLog;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;

public class PendingInvitationResolution {
    private final static String TAG = PendingInvitationResolution.class.getSimpleName();
    public static InvitationBundle incomingInvitation;
    public static boolean accepted = false;

    public static void onIncomingInvitation(Context context, InvitationBundle incomingInvitation) {
        VoxeetLog.log(TAG, "onIncomingInvitation: " + incomingInvitation);
        PendingInvitationResolution.incomingInvitation = incomingInvitation;
        accepted = false;

        Intent intent = IntentUtils.createIntent(context, incomingInvitation);
        context.startActivity(intent);
    }

    public static void onCancelNotification(@NonNull Context context, @NonNull String conferenceId) {
        int notificationId = conferenceId.hashCode();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (-1 != notificationId) notificationManager.cancel(notificationId);
        notificationId = 0;
    }

    public static void onIncomingInvitationAccepted(Context context) {
        VoxeetLog.log(TAG, "onIncomingInvitationAccepted: " + incomingInvitation + " " + accepted);
        if (null == incomingInvitation || accepted) return;

        InvitationBundle invitationBundle = PendingInvitationResolution.incomingInvitation;
        accepted = true;
        //PendingInvitationResolution.incomingInvitation = null;
        onCancelNotification(context, invitationBundle.conferenceId);

        Intent intent = IntentUtils.createIntent(context, invitationBundle);
        VoxeetLog.log(TAG, "onIncomingInvitationAccepted: intent " + intent);

        RNVoxeetActivity activity = RNVoxeetActivityObject.getActivity();

        if (null == activity) {
            VoxeetLog.log(TAG, "onIncomingInvitationAccepted: startActivity");
            context.startActivity(intent);
        } else {
            VoxeetLog.log(TAG, "onIncomingInvitationAccepted: direct call to activity");
            activity.onInvitationBundle(intent);

            // and bring to front
            Intent bring = new Intent(context, activity.getClass());
            bring.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(bring);
        }
    }

    public static void flushPendingInvitation(Context context) {
        InvitationBundle invitationBundle = PendingInvitationResolution.incomingInvitation;
        if (null == invitationBundle) return;

        accepted = true;
        PendingInvitationResolution.incomingInvitation = null;
        onCancelNotification(context, invitationBundle.conferenceId);
    }

    public static void onForwardToIncomingCall(Activity activity) {
        VoxeetLog.log(TAG, "onForwardToIncomingCall: " + incomingInvitation + " " + accepted);
        if (null == incomingInvitation || accepted) return;

        RNVoxeetFirebaseIncomingNotification manage = new RNVoxeetFirebaseIncomingNotification();
        manage.onInvitation(activity, incomingInvitation);
    }

}
