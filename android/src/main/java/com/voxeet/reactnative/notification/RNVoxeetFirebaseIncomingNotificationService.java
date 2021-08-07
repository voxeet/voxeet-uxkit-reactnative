package com.voxeet.reactnative.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.app.Service;
import android.os.IBinder;

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

import java.security.SecureRandom;

public class RNVoxeetFirebaseIncomingNotificationService extends Service {

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

    @Override
    public void onCreate() {
        random = new SecureRandom();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        InvitationBundle serviceInvitationBundle = new InvitationBundle(bundle);
        notificationId = random.nextInt(Integer.MAX_VALUE / 2);
        if (null != (serviceInvitationBundle ).conferenceId) {
            notificationId = serviceInvitationBundle.conferenceId.hashCode();
        }

        String channelId = getChannelId(this);

        Intent accept = createAcceptIntent(this, serviceInvitationBundle);
        Intent dismiss = createDismissIntent(this, serviceInvitationBundle);
        Intent callingIntent = createCallingIntent(this, serviceInvitationBundle);

        if (null != accept) accept.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        dismiss.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        callingIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        if (null == accept) {
            Log.d(TAG, "onInvitation: accept intent is null !! did you set the voxeet_incoming_accepted_class prop");
            return Service.START_STICKY;
        }

        PendingIntent pendingIntentAccepted = PendingIntent.getBroadcast(this, INCOMING_NOTIFICATION_REQUEST_CODE, accept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentDismissed = PendingIntent.getBroadcast(this, INCOMING_NOTIFICATION_REQUEST_CODE, dismiss, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingCallingIntent = PendingIntent.getActivity(this, INCOMING_NOTIFICATION_REQUEST_CODE, callingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +this.getPackageName()+"/"+R.raw.google_pixel_zen);

        String inviterName = Opt.of(serviceInvitationBundle.inviter).then(ParticipantNotification::getInfo).then(ParticipantInfo::getName).or("");
        Notification lastNotification = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingCallingIntent, true)
                .setSound(soundUri)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentTitle(this.getString(R.string.voxeet_incoming_notification_from_user, inviterName))
                .setContentText(this.getString(R.string.voxeet_incoming_notification_accept))
                .setSmallIcon(R.drawable.ic_incoming_call_notification)
                .addAction(R.drawable.ic_incoming_call_dismiss, this.getString(R.string.voxeet_incoming_notification_button_dismiss), pendingIntentDismissed)
                .addAction(R.drawable.ic_incoming_call_accept, this.getString(R.string.voxeet_incoming_notification_button_accept), pendingIntentAccepted)
                .setAutoCancel(IncomingNotification.Configuration.IsAutoCancel)
                .setOngoing(IncomingNotification.Configuration.IsOnGoing)
                .build();

        startForeground(notificationId, lastNotification);

        VoxeetLog.log(TAG, "showing notification overhead");
        return Service.START_STICKY;
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
        Intent intent = new Intent(context, InvitationDismissBroadcastReceiver.class);

        for (String key : IncomingFullScreen.DEFAULT_NOTIFICATION_KEYS) {
            if (extra.containsKey(key)) {
                intent.putExtra(key, extra.getString(key));
            }
        }

        return intent;
    }

    @NonNull
    private Intent createCallingIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Bundle extra = invitationBundle.asBundle();
        Intent intent = new Intent(this, RNIncomingCallActivity.class);

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
