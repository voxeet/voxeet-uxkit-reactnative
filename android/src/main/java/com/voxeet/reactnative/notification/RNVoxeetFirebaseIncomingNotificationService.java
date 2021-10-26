package com.voxeet.reactnative.notification;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.app.Service;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.voxeet.reactnative.R;
import com.voxeet.reactnative.utils.VoxeetLog;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.models.ParticipantNotification;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.Opt;
import com.voxeet.uxkit.incoming.IncomingFullScreen;
import com.voxeet.uxkit.incoming.IncomingNotification;
import com.voxeet.uxkit.incoming.IncomingNotificationConfiguration;
import com.voxeet.uxkit.incoming.manifest.DismissNotificationBroadcastReceiver;

import java.security.SecureRandom;

public class RNVoxeetFirebaseIncomingNotificationService extends Service {

    //extracted from the sdk
    //TODO set in the push module not the push_manifest one
    private static final String SDK_CHANNEL_ID = "voxeet_sdk_incoming_channel_id";
    public static final String DEFAULT_ID = "IncomingVideoConference";

    private final static int DEFAULT_NOTIFICATION_ID = 234;
    public final static int INCOMING_NOTIFICATION_REQUEST_CODE = 98;
    private static final String TAG = IncomingNotification.class.getSimpleName();
    public final static String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";

    // will hold the various static configuration for the IncomingNotification
    // to edit, preferrably use either Factory component in the manifest or Application override when dealing with FCM
    public final static IncomingNotificationConfiguration Configuration = new IncomingNotificationConfiguration();


    private SecureRandom random;
    //private int notificationId = -1;

    public static void stop(@NonNull Context context) {
        RNVoxeetFirebaseIncomingNotificationService.stop(context, null, null);
    }

    public static void stop(@NonNull Context context, @Nullable String conferenceId, @Nullable Bundle bundle) {
        if(null != conferenceId) {
            int notificationId = conferenceId.hashCode();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (-1 != notificationId) notificationManager.cancel(notificationId);
        }

        //TODO manage the case where conferenceId are different from the one creating this notification
        Intent intent = new Intent(context, RNVoxeetFirebaseIncomingNotificationService.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        context.stopService(intent);
    }

    public static void start(@NonNull Context context, @NonNull InvitationBundle invitation) {
        if(!isBackgroundRestricted(context)) {
            Intent intent = new Intent(context, RNVoxeetFirebaseIncomingNotificationService.class);
            intent.putExtras(invitation.asBundle());
            ContextCompat.startForegroundService(context, intent);
        } else {
            Notification notification = createNotification(context, invitation);
            if(null == notification) {
                Log.d(TAG, "start: invalid notification obtained,; skipping");
                return;
            }
            int notificationId = Opt.of(invitation.conferenceId).then(String::hashCode).or(DEFAULT_NOTIFICATION_ID);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        random = new SecureRandom();

        startForegroundDefault();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        InvitationBundle serviceInvitationBundle = new InvitationBundle(bundle);
        Notification lastNotification = createNotification(this, serviceInvitationBundle);

        int notificationId = Opt.of(serviceInvitationBundle.conferenceId).then(String::hashCode).or(DEFAULT_NOTIFICATION_ID);
        if(null == lastNotification) {
            stopSelf();
            return START_STICKY;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, lastNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
        } else {
            startForeground(notificationId, lastNotification);
        }

        VoxeetLog.log(TAG, "showing notification overhead");
        return Service.START_STICKY;
    }

    @Nullable
    private static Intent createAcceptIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
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
    private static Intent createDismissIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Bundle extra = invitationBundle.asBundle();
        Intent intent = new Intent(context, DismissNotificationBroadcastReceiver.class);

        for (String key : IncomingFullScreen.DEFAULT_NOTIFICATION_KEYS) {
            if (extra.containsKey(key)) {
                intent.putExtra(key, extra.getString(key));
            }
        }

        return intent;
    }

    @NonNull
    private static Intent createCallingIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Bundle extra = invitationBundle.asBundle();
        Intent intent = new Intent(context, RNIncomingCallActivity.class);

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
        if (Build.VERSION.SDK_INT >= 26) {
            String channelId = getChannelId(context);

            NotificationChannel channel = new NotificationChannel(channelId,
                    context.getString(R.string.voxeet_channel_title),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.voxeet_channel_description));
            channel.enableLights(true);
            channel.setLightColor(0);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100L, 200L});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/incoming_call");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != mNotificationManager) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        return true;
    }

    public static Notification createNotification(Context context, InvitationBundle serviceInvitationBundle) {
        int notificationId = DEFAULT_NOTIFICATION_ID;
        if (null != serviceInvitationBundle.conferenceId) {
            notificationId = serviceInvitationBundle.conferenceId.hashCode();
        }

        String channelId = getChannelId(context);

        Intent accept = createAcceptIntent(context, serviceInvitationBundle);
        Intent dismiss = createDismissIntent(context, serviceInvitationBundle);
        Intent callingIntent = createCallingIntent(context, serviceInvitationBundle);

        if (null != accept) accept.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        dismiss.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        callingIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        if (null == accept) {
            return null;
        }

        PendingIntent pendingIntentAccepted = PendingIntent.getBroadcast(context, INCOMING_NOTIFICATION_REQUEST_CODE, accept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentDismissed = PendingIntent.getBroadcast(context, INCOMING_NOTIFICATION_REQUEST_CODE, dismiss, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingCallingIntent = PendingIntent.getActivity(context, INCOMING_NOTIFICATION_REQUEST_CODE, callingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String inviterName = Opt.of(serviceInvitationBundle.inviter).then(ParticipantNotification::getInfo).then(ParticipantInfo::getName).or("");
        return new NotificationCompat.Builder(context, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingCallingIntent, true)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentTitle(context.getString(R.string.voxeet_incoming_notification_from_user, inviterName))
                .setContentText(context.getString(R.string.voxeet_incoming_notification_accept))
                .setSmallIcon(R.drawable.ic_incoming_call_notification)
                .addAction(R.drawable.ic_incoming_call_dismiss, context.getString(R.string.voxeet_incoming_notification_button_dismiss), pendingIntentDismissed)
                .addAction(R.drawable.ic_incoming_call_accept, context.getString(R.string.voxeet_incoming_notification_button_accept), pendingIntentAccepted)
                .setAutoCancel(IncomingNotification.Configuration.IsAutoCancel)
                .setOngoing(IncomingNotification.Configuration.IsOnGoing)
                .build();
    }

    private void startForegroundDefault() {
        int notificationId = DEFAULT_NOTIFICATION_ID;
        String channelId = getChannelId(this);
        Log.d(TAG, "startForegroundDefault: " + channelId);

        Notification lastNotification = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentTitle("starting")
                .setContentText("starting")
                .setSmallIcon(R.drawable.ic_incoming_call_notification)
                .setOngoing(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //shouldn't happen, creating overhead above
            startForeground(notificationId, lastNotification,
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                    | android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        } else {
            startForeground(notificationId, lastNotification);
        }
    }

    public static boolean isBackgroundRestricted(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return activityManager.isBackgroundRestricted();
        }
        return false;
    }
}
