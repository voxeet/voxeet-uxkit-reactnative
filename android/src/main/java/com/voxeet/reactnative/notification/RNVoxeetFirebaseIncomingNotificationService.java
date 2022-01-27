package com.voxeet.reactnative.notification;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.uxkit.common.UXKitLogger;
import com.voxeet.uxkit.common.logging.ShortLogger;
import com.voxeet.uxkit.incoming.AbstractIncomingNotificationIntentProvider;
import com.voxeet.uxkit.incoming.AbstractIncomingNotificationService;
import com.voxeet.uxkit.incoming.utils.IncomingNotificationServiceHelper;

public class RNVoxeetFirebaseIncomingNotificationService extends AbstractIncomingNotificationService {

    private final static ShortLogger Log = UXKitLogger.createLogger(RNVoxeetFirebaseIncomingNotificationService.class);

    public static void stop(@NonNull Context context) {
        RNVoxeetFirebaseIncomingNotificationService.stop(context, null, null);
    }

    public static void stop(@NonNull Context context, @Nullable String conferenceId, @Nullable Bundle bundle) {

        IncomingNotificationServiceHelper.stop(RNVoxeetFirebaseIncomingNotificationService.class,
                context, conferenceId, bundle);
    }

    public static void start(@NonNull Context context, @NonNull InvitationBundle invitation) {
        IncomingNotificationServiceHelper.start(RNVoxeetFirebaseIncomingNotificationService.class,
                context, invitation, new RNIncomingNotificationProvider(context, Log));
    }

    @NonNull
    @Override
    protected AbstractIncomingNotificationIntentProvider createIncomingNotificationIntentProvider() {
        return new RNIncomingNotificationProvider(this, Log);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("receiving onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

/*
    public static createNotificationChannel(@NonNull Context context) {
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
    }
 */
}
