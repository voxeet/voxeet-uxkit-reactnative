package com.voxeet.reactnative.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.voxeet.reactnative.specifics.RNVoxeetActivity;
import com.voxeet.reactnative.specifics.RNVoxeetActivityObject;
import com.voxeet.reactnative.utils.IntentUtils;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.uxkit.common.UXKitLogger;

public class PendingInvitationResolution {
    private final static String TAG = PendingInvitationResolution.class.getSimpleName();
    public static InvitationBundle incomingInvitation;
    public static boolean accepted = false;

    public static void onIncomingInvitation(Context context, InvitationBundle incomingInvitation) {
        UXKitLogger.d(TAG, "onIncomingInvitation: " + incomingInvitation);
        PendingInvitationResolution.incomingInvitation = incomingInvitation;
        accepted = false;

        Intent intent = IntentUtils.createIntent(context, incomingInvitation);
        context.startActivity(intent);
    }

    public static void onCancelNotification(@NonNull Context context, @NonNull InvitationBundle incomingInvitation) {
        RNVoxeetFirebaseIncomingNotificationService.stop(context, incomingInvitation.conferenceId, incomingInvitation.asBundle());
    }

    public static void onIncomingInvitationAccepted(Context context) {
        UXKitLogger.d(TAG, "onIncomingInvitationAccepted: " + incomingInvitation + " " + accepted);
        if (null == incomingInvitation || accepted) return;

        accepted = true;
        InvitationBundle invitationBundle = PendingInvitationResolution.incomingInvitation;
        onCancelNotification(context, invitationBundle);

        Intent intent = IntentUtils.createIntent(context, invitationBundle);
        UXKitLogger.d(TAG, "onIncomingInvitationAccepted: intent " + intent);

        RNVoxeetActivity activity = RNVoxeetActivityObject.getActivity();

        if (null == activity) {
            UXKitLogger.d(TAG, "onIncomingInvitationAccepted: startActivity");
            context.startActivity(intent);
        } else {
            UXKitLogger.d(TAG, "onIncomingInvitationAccepted: direct call to activity");
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
        onCancelNotification(context, invitationBundle);
    }

    public static void onForwardToIncomingCall(Context context, Activity activity) {
        UXKitLogger.d(TAG, "onForwardToIncomingCall: " + incomingInvitation + " " + accepted);
        if (null == incomingInvitation || accepted) return;

        RNVoxeetFirebaseIncomingNotificationService.start(context, incomingInvitation);
    }

}
