package com.voxeet.reactnative.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.voxeet.VoxeetSDK;
import com.voxeet.promise.Promise;
import com.voxeet.sdk.exceptions.VoxeetSDKNotInitiliazedException;
import com.voxeet.sdk.push.center.NotificationCenter;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.SessionService;

public class InvitationDismissBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        InvitationBundle invitationBundle = null;

        if (null != bundle) invitationBundle = new InvitationBundle(bundle);

        //TODO create a way to register for dismissed conference invitation when the sdk is unintialized
        if (null != invitationBundle && null != invitationBundle.conferenceId) {
            InvitationBundle finalInvitationBundle = invitationBundle;
            createPromise(invitationBundle.conferenceId).then((result, solver) -> {
                NotificationCenter.instance.onInvitationCanceledReceived(context, finalInvitationBundle.conferenceId);
            }).error(error -> NotificationCenter.instance.onInvitationCanceledReceived(context, finalInvitationBundle.conferenceId));
        }
    }

    private Promise<Boolean> createPromise(@NonNull String conferenceId) {
        ConferenceService conferenceService = VoxeetSDK.conference();
        SessionService sessionService = VoxeetSDK.session();
        if (!VoxeetSDK.instance().isInitialized()) {
            return new Promise<>(solver -> solver.reject(new VoxeetSDKNotInitiliazedException("SDK Uninitialized in " + InvitationDismissBroadcastReceiver.class.getSimpleName())));
        }

        if (sessionService.isSocketOpen()) {
            return conferenceService.decline(conferenceId);
        } else {
            //TODO refactor with open decline resolve error
            return new Promise<>(solver -> sessionService.open().then((result, internal_solver) -> conferenceService.decline(conferenceId)
                    .then((result1, internal_solver1) -> solver.resolve(result1))
                    .error(solver::reject)
            ).error(solver::reject));
        }
    }
}
