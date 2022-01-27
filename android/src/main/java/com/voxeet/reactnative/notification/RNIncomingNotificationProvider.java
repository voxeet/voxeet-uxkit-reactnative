package com.voxeet.reactnative.notification;

import android.content.BroadcastReceiver;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.voxeet.uxkit.common.logging.ShortLogger;
import com.voxeet.uxkit.incoming.AbstractIncomingNotificationIntentProvider;
import com.voxeet.uxkit.incoming.manifest.DismissNotificationBroadcastReceiver;

public class RNIncomingNotificationProvider extends AbstractIncomingNotificationIntentProvider {

    public RNIncomingNotificationProvider(@NonNull Context context, @NonNull ShortLogger log) {
        super(context, log);
    }

    @NonNull
    @Override
    protected Class<? extends BroadcastReceiver> getAcceptedBroadcastReceiverClass() {
        return InvitationAcceptedBroadcastReceiver.class;
    }

    @NonNull
    @Override
    protected Class<? extends BroadcastReceiver> getDismissedBroadcastReceiverClass() {
        return DismissNotificationBroadcastReceiver.class;
    }

    @Nullable
    @Override
    protected Class<? extends AppCompatActivity> getIncomingCallActivityClass() {
        return RNIncomingCallActivity.class;
    }
}
