package com.voxeet.reactnative.notification;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.voxeet.VoxeetSDK;
import com.voxeet.reactnative.utils.RNPermissionHelper;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.sdk.json.ConferenceEnded;
import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.NotificationService;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.Opt;
import com.voxeet.uxkit.common.activity.PermissionContractHolder;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;
import com.voxeet.uxkit.common.activity.bundle.IExtraBundleFillerListener;
import com.voxeet.uxkit.common.activity.bundle.IncomingBundleChecker;
import com.voxeet.uxkit.common.permissions.PermissionController;
import com.voxeet.uxkit.common.permissions.PermissionResult;
import com.voxeet.uxkit.views.internal.rounded.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

public class RNIncomingCallActivity extends AppCompatActivity implements IExtraBundleFillerListener {

    private final static String TAG = RNIncomingCallActivity.class.getSimpleName();
    private static final String DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY = "voxeet_incoming_call_duration";
    private static final int DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE = 40 * 1000;

    protected TextView mUsername;
    protected TextView mStateTextView;
    protected TextView mDeclineTextView;
    protected TextView mAcceptTextView;
    protected RoundedImageView mAvatar;
    protected EventBus mEventBus;

    private IncomingBundleChecker incomingBundleChecker;
    private Handler mHandler;
    private PermissionContractHolder permissionContractHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        incomingBundleChecker = new DefaultIncomingBundleChecker(getIntent(), this);
        permissionContractHolder = new PermissionContractHolder(this);


        //add few Flags to start the activity before its setContentView
        //note that if your device is using a keyguard (code or password)
        //when the call will be accepted, you still need to unlock it
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(com.voxeet.uxkit.R.layout.voxeet_activity_incoming_call);

        mUsername = findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_username);
        mAvatar = findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_avatar_image);
        mStateTextView = findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_text);
        mAcceptTextView = findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_accept);
        mDeclineTextView = findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_decline);

        mDeclineTextView.setOnClickListener(view -> onDecline());

        mAcceptTextView.setOnClickListener(view -> onAccept());

        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            try {
                if (null != mHandler)
                    finish();
            } catch (Exception e) {

            }
        }, AndroidManifest.readMetadataInt(this, DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY,
                DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionController.register(permissionContractHolder.getRequestPermissions());

        if (incomingBundleChecker.isBundleValid()) {
            VoxeetSDK instance = VoxeetSDK.instance();
            mEventBus = instance.getEventBus();
            if (null != mEventBus) mEventBus.register(this);

            mUsername.setText(incomingBundleChecker.getUserName());
            try {
                if (!TextUtils.isEmpty(incomingBundleChecker.getAvatarUrl())) {
                    Picasso.get()
                            .load(incomingBundleChecker.getAvatarUrl())
                            .into(mAvatar);
                }
            } catch (Exception e) {

            }
        } else {
            //Toast.makeText(this, getString(com.voxeet.uxkit.R.string.invalid_bundle), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {

        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPush event) {
        if (incomingBundleChecker.isSameConference(event.conferenceId)) {
            finish();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceEnded event) {
        if (incomingBundleChecker.isSameConference(event.conferenceId)) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        switch (event.state) {
            case JOINING:
                String conferenceId = Opt.of(event).then(e -> e.conference).then(Conference::getId).orNull();
                if (incomingBundleChecker.isSameConference(conferenceId)) {
                    finish();
                }
            default:
        }
    }

    @Nullable
    protected String getConferenceId() {
        return incomingBundleChecker != null && incomingBundleChecker.isBundleValid() ? incomingBundleChecker.getConferenceId() : null;
    }

    protected void onDecline() {
        ConferenceService conferenceService = VoxeetSDK.conference();
        NotificationService notificationService = VoxeetSDK.notification();

        String conferenceId = getConferenceId();
        Conference conference = Opt.of(conferenceId)
                .then(conferenceService::getConference).orNull();

        RNVoxeetFirebaseIncomingNotificationService.stop(this, conferenceId, null);

        if (null != conference) {
            notificationService.decline(conference)
                    .then(result -> {
                        finish();
                    })
                    .error(error -> finish());
        } else {
            finish();
        }
    }

    protected void onAccept() {
        String conferenceId = getConferenceId();
        RNVoxeetFirebaseIncomingNotificationService.stop(this, conferenceId, null);

        if (incomingBundleChecker.isBundleValid()) {

            RNPermissionHelper.requestDefaultPermission().then(ok -> {
                if(!ok) throw new IllegalStateException("no mic permission");

                PendingInvitationResolution.onIncomingInvitationAccepted(RNIncomingCallActivity.this);
                //REACT_NATIVE_ROOT_BUNDLE = mIncomingBundleChecker;

                //Intent intent = mIncomingBundleChecker.createRNActivityAccepted(this);
                ////start the accepted call activity
                //startActivity(intent);

                //and finishing this one - before the prejoined event
                finish();
                overridePendingTransition(0, 0);
            }).error(Throwable::printStackTrace);
        }
    }

    /**
     * Give the possibility to add custom extra infos before starting a conference
     *
     * @return a nullable extra bundle (will not be the bundle sent but a value with a key)
     */
    @Nullable
    @Override
    public Bundle createExtraBundle() {
        //override to return a custom intent to add in the possible notification
        //note that everything which could have been backed up from the previous activity
        //will be injected after the creation - usefull if the app is mainly based on
        //passed intents
        return null;
    }

    /**
     * Get the instance of the bundle checker corresponding to this activity
     *
     * @return an instance or null corresponding to the current bundle checker
     */
    @Nullable
    protected IncomingBundleChecker getBundleChecker() {
        return incomingBundleChecker;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
