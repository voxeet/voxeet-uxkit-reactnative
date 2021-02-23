package com.voxeet.notification;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.voxeet.VoxeetSDK;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.sdk.json.ConferenceEnded;
import com.voxeet.sdk.media.audio.AudioRoute;
import com.voxeet.sdk.media.audio.SoundManager;
import com.voxeet.sdk.services.AudioService;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.AudioType;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.uxkit.views.internal.rounded.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RNIncomingCallActivity extends AppCompatActivity implements RNIncomingBundleChecker.IExtraBundleFillerListener {

    private final static String TAG = RNIncomingCallActivity.class.getSimpleName();
    private static final String DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY = "voxeet_incoming_call_duration";
    private static final int DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE = 40 * 1000;
    public static RNIncomingBundleChecker REACT_NATIVE_ROOT_BUNDLE = null;

    protected TextView mUsername;
    protected TextView mStateTextView;
    protected TextView mDeclineTextView;
    protected TextView mAcceptTextView;
    protected RoundedImageView mAvatar;
    protected EventBus mEventBus;

    private RNIncomingBundleChecker mIncomingBundleChecker;
    private Handler mHandler;
    private boolean isResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isResumed = false;

        //we preInit the AudioService,
        AudioService.preInitSounds(getApplicationContext());

        mIncomingBundleChecker = new RNIncomingBundleChecker(this, getIntent(), this);

        //add few Flags to start the activity before its setContentView
        //note that if your device is using a keyguard (code or password)
        //when the call will be accepted, you still need to unlock it
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(com.voxeet.uxkit.R.layout.voxeet_activity_incoming_call);

        mUsername = (TextView) findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_username);
        mAvatar = (RoundedImageView) findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_avatar_image);
        mStateTextView = (TextView) findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_text);
        mAcceptTextView = (TextView) findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_accept);
        mDeclineTextView = (TextView) findViewById(com.voxeet.uxkit.R.id.voxeet_incoming_decline);

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
        isResumed = true;

        SoundManager soundManager = AudioService.getSoundManager();
        if (null != soundManager) {
            soundManager.checkOutputRoute().playSoundType(AudioType.RING);
            soundManager.setAudioRoute(AudioRoute.ROUTE_SPEAKER);
        }

        if (mIncomingBundleChecker.isBundleValid()) {
            VoxeetSDK instance = VoxeetSDK.instance();
            mEventBus = instance.getEventBus();
            if (null != mEventBus) mEventBus.register(this);

            mUsername.setText(mIncomingBundleChecker.getUserName());
            Picasso.get()
                    .load(mIncomingBundleChecker.getAvatarUrl())
                    .into(mAvatar);
        } else {
            Toast.makeText(this, getString(com.voxeet.uxkit.R.string.invalid_bundle), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;

        SoundManager soundManager = AudioService.getSoundManager();
        if (null != soundManager) {
            soundManager.resetDefaultSoundType().stopSoundType(AudioType.RING);
        }


        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionRefusedEvent.RESULT_CAMERA: {
                ConferenceService conferenceService = VoxeetSDK.conference();
                if (conferenceService.isLive()) {
                    conferenceService.startVideo()
                            .then(result -> {

                            })
                            .error(Throwable::printStackTrace);
                }
                return;
            }
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPush event) {
        if (mIncomingBundleChecker.isSameConference(event.conferenceId)) {
            finish();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceEnded event) {
        if (mIncomingBundleChecker.isSameConference(event.conferenceId)) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        switch (event.state) {
            case JOINING:
                if (mIncomingBundleChecker.isSameConference(event.conference)) {
                    finish();
                }
            default:
        }
    }

    @Nullable
    protected String getConferenceId() {
        return mIncomingBundleChecker != null && mIncomingBundleChecker.isBundleValid() ? mIncomingBundleChecker.getConferenceId() : null;
    }

    protected void onDecline() {
        ConferenceService conferenceService = VoxeetSDK.conference();
        if (null != getConferenceId()) {
            conferenceService.decline(getConferenceId())
                    .then(result -> {
                        finish();
                    })
                    .error(error -> finish());
        } else {
            finish();
        }
    }

    protected void onAccept() {

        if (!Validate.hasMicrophonePermissions(this)) {
            Validate.requestMandatoryPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            }, 42);
            return;
        }

        if (mIncomingBundleChecker.isBundleValid()) {
            REACT_NATIVE_ROOT_BUNDLE = mIncomingBundleChecker;

            Intent intent = mIncomingBundleChecker.createActivityAccepted(this);
            //start the accepted call activity
            startActivity(intent);

            //and finishing this one - before the prejoined event
            finish();
            overridePendingTransition(0, 0);
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
    protected RNIncomingBundleChecker getBundleChecker() {
        return mIncomingBundleChecker;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
