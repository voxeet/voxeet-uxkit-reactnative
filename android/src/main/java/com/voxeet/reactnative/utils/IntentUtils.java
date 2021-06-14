package com.voxeet.reactnative.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.reactnative.specifics.RNVoxeetActivity;
import com.voxeet.reactnative.specifics.RNVoxeetActivityObject;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.Opt;
import com.voxeet.uxkit.incoming.IncomingFullScreen;
import com.voxeet.uxkit.incoming.factory.IncomingCallFactory;

public class IntentUtils {

    private static String getIncomingAcceptedClass(@NonNull Context context) {
        return AndroidManifest.readMetadata(context, "voxeet_incoming_accepted_class", null);
    }

    @Nullable
    public static Intent createIntent(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Bundle extra = invitationBundle.asBundle();

        RNVoxeetActivity activity = RNVoxeetActivityObject.getActivity();
        Class klass = Opt.of(activity).then(Activity::getClass).orNull();

        if (null == klass) klass = IncomingCallFactory.getAcceptedIncomingActivityKlass();

        if (null == klass) {
            String klass_fully_qualified = getIncomingAcceptedClass(context);
            if (null != klass_fully_qualified) {
                try {
                    klass = Class.forName(klass_fully_qualified);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        //we have an invalid klass, returning null
        if (null == klass) return null;

        Intent intent = new Intent(context, klass);

        //inject the extras from the current "loaded" activity
        Bundle extras = IncomingCallFactory.getAcceptedIncomingActivityExtras();
        if (null != extras) {
            intent.putExtras(extras);
        }

        for (String key : IncomingFullScreen.DEFAULT_NOTIFICATION_KEYS) {
            if (extra.containsKey(key)) {
                intent.putExtra(key, extra.getString(key));
            }
        }

        if (null == activity) {
            Log.d("VoxeetSDK", "createIntent: will be a new task");
            //create a new one
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            Log.d("VoxeetSDK", "createIntent: will bring existing one");
            activity.onInvitationBundle(intent);
        }

        return intent;
    }
}
