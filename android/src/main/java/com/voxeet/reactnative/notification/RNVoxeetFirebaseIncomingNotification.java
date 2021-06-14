package com.voxeet.reactnative.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.voxeet.reactnative.R;
import com.voxeet.reactnative.utils.VoxeetLog;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.models.ParticipantNotification;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.push.utils.NotificationHelper;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.Opt;
import com.voxeet.uxkit.incoming.IncomingFullScreen;
import com.voxeet.uxkit.incoming.IncomingNotification;
import com.voxeet.uxkit.incoming.IncomingNotificationConfiguration;
import com.voxeet.uxkit.incoming.manifest.DismissNotificationBroadcastReceiver;

import java.security.SecureRandom;

public class RNVoxeetFirebaseIncomingNotification {

    //extracted from the sdk
    //TODO set in the push module not the push_manifest one
    private static final String SDK_CHANNEL_ID = "voxeet_sdk_channel_id";
    private static final String DEFAULT_ID = "VideoConference";

    public final static int INCOMING_NOTIFICATION_REQUEST_CODE = 98;
    private static final String TAG = IncomingNotification.class.getSimpleName();
    public final static String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";

    // will hold the various static configuration for the IncomingNotification
    // to edit, preferrably use either Factory component in the manifest or Application override when dealing with FCM
    public final static IncomingNotificationConfiguration Configuration = new IncomingNotificationConfiguration();

    private SecureRandom random;
    private int notificationId = -1;

    public RNVoxeetFirebaseIncomingNotification() {
        random = new SecureRandom();
    }

    public void onInvitation(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        notificationId = random.nextInt(Integer.MAX_VALUE / 2);
        if (null != invitationBundle.conferenceId) {
            notificationId = invitationBundle.conferenceId.hashCode();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = getChannelId(context);

        Intent accept = createAcceptIntent(context, invitationBundle);
        Intent dismiss = createDismissIntent(context, invitationBundle);

        if (null != accept) accept.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        dismiss.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        if (null == accept) {
            Log.d(TAG, "onInvitation: accept intent is null !! did you set the voxeet_incoming_accepted_class prop");
            return;
        }

        PendingIntent pendingIntentAccepted = PendingIntent.getBroadcast(context, INCOMING_NOTIFICATION_REQUEST_CODE, accept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentDismissed = PendingIntent.getBroadcast(context, INCOMING_NOTIFICATION_REQUEST_CODE, dismiss, PendingIntent.FLAG_UPDATE_CURRENT);

        String inviterName = Opt.of(invitationBundle.inviter).then(ParticipantNotification::getInfo).then(ParticipantInfo::getName).or("");

        Notification lastNotification = new NotificationCompat.Builder(context, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(context.getString(R.string.voxeet_incoming_notification_from_user, inviterName))
                .setContentText(context.getString(R.string.voxeet_incoming_notification_accept))
                .setSmallIcon(R.drawable.ic_incoming_call_notification)
                .addAction(R.drawable.ic_incoming_call_dismiss, context.getString(R.string.voxeet_incoming_notification_button_dismiss), pendingIntentDismissed)
                .addAction(R.drawable.ic_incoming_call_accept, context.getString(R.string.voxeet_incoming_notification_button_accept), pendingIntentAccepted)
                .setAutoCancel(IncomingNotification.Configuration.IsAutoCancel)
                .setOngoing(IncomingNotification.Configuration.IsOnGoing)
                .build();
        //TODO Android Use Full Screen Intent with according permission -> possible improvement

        notificationManager.notify(notificationId, lastNotification);

        VoxeetLog.log(TAG, "showing notification overhead");
    }

    @Nullable
    private Intent createAcceptIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Bundle extra = invitationBundle.asBundle();
        Intent intent = new Intent(context, InvitationAcceptedBroadcastReceiver.class);

        for (String key : IncomingFullScreen.DEFAULT_NOTIFICATION_KEYS) {
            if (extra.containsKey(key)) {
                intent.putExtra(key, extra.getString(key));
            }
        }

        return intent;
    }

    @NonNull
    private Intent createDismissIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Bundle extra = invitationBundle.asBundle();
        Intent intent = new Intent(context, DismissNotificationBroadcastReceiver.class);

        for (String key : IncomingFullScreen.DEFAULT_NOTIFICATION_KEYS) {
            if (extra.containsKey(key)) {
                intent.putExtra(key, extra.getString(key));
            }
        }

        return intent;
    }

    public static String getChannelId(@NonNull Context context) {
        return AndroidManifest.readMetadata(context, SDK_CHANNEL_ID, DEFAULT_ID);
    }

    public static boolean createNotificationChannel(@NonNull Context context) {
        return NotificationHelper.createNotificationChannel(context,
                DEFAULT_ID,
                context.getString(R.string.voxeet_channel_title),
                context.getString(R.string.voxeet_channel_description),
                0);
    }
}
