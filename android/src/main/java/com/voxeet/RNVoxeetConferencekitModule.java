
package com.voxeet;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.voxeet.models.ConferenceUtil;
import com.voxeet.notification.RNIncomingBundleChecker;
import com.voxeet.notification.RNIncomingCallActivity;
import com.voxeet.sdk.authent.token.TokenCallback;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.sdk.SocketStateChangeEvent;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.json.internal.MetadataHolder;
import com.voxeet.sdk.json.internal.ParamsHolder;
import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.push.center.NotificationCenter;
import com.voxeet.sdk.push.center.management.EnforcedNotificationMode;
import com.voxeet.sdk.push.center.management.NotificationMode;
import com.voxeet.sdk.push.center.management.VersionFilter;
import com.voxeet.sdk.push.center.subscription.register.SubscribeInvitation;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.SessionService;
import com.voxeet.sdk.services.TelemetryService;
import com.voxeet.sdk.services.builders.ConferenceCreateOptions;
import com.voxeet.sdk.services.telemetry.SdkEnvironment;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.sdk.utils.VoxeetEnvironmentHolder;
import com.voxeet.specifics.RNRootViewProvider;
import com.voxeet.specifics.RNVoxeetActivity;
import com.voxeet.specifics.waiting.WaitingAbstractHolder;
import com.voxeet.specifics.waiting.WaitingJoinHolder;
import com.voxeet.specifics.waiting.WaitingStartConferenceHolder;
import com.voxeet.uxkit.controllers.ConferenceToolkitController;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;
import com.voxeet.uxkit.incoming.IncomingFullScreen;
import com.voxeet.uxkit.incoming.IncomingNotification;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RNVoxeetConferencekitModule extends ReactContextBaseJavaModule {

    private static final String ERROR_SDK_NOT_INITIALIZED = "ERROR_SDK_NOT_INITIALIZED";
    private static final String ERROR_SDK_NOT_LOGGED_IN = "ERROR_SDK_NOT_LOGGED_IN";

    private final static String TAG = RNVoxeetConferencekitModule.class.getSimpleName();

    public static boolean startVideo;

    @Nullable
    private static RNVoxeetActivity sActivity;

    private final ReactApplicationContext reactContext;
    private final RNRootViewProvider mRootViewProvider;
    private ParticipantInfo _current_user;
    private ReentrantLock lockAwaitingToken = new ReentrantLock();
    private List<TokenCallback> mAwaitingTokenCallback;
    private static WaitingAbstractHolder sWaitingHolder;
    //private Callback refreshAccessTokenCallbackInstance;

    public RNVoxeetConferencekitModule(RNRootViewProvider rootViewProvider, ReactApplicationContext reactContext) {
        super(reactContext);
        mRootViewProvider = rootViewProvider;
        mAwaitingTokenCallback = new ArrayList<>();
        this.reactContext = reactContext;

        VoxeetPreferences.init(reactContext, new VoxeetEnvironmentHolder(reactContext));
        TelemetryService.register(SdkEnvironment.REACT_NATIVE, BuildConfig.VERSION_NAME);
    }

    @Override
    public String getName() {
        return "RNVoxeetConferencekit";
    }

    @ReactMethod
    public void debug(Promise promise) {
        promise.resolve("some string from Android");
    }


    @ReactMethod
    public void initializeToken(String accessToken, Promise promise) {
        Application application = (Application) reactContext.getApplicationContext();

        if (null == VoxeetSDK.instance()) {
            //refreshAccessTokenCallbackInstance = callback;

            VoxeetSDK.setApplication(application);
            VoxeetSDK.initialize(
                    accessToken,
                    callback -> {
                        lock(lockAwaitingToken);
                        if (!mAwaitingTokenCallback.contains(callback)) {
                            mAwaitingTokenCallback.add(callback);
                        }
                        unlock(lockAwaitingToken);
                        postRefreshAccessToken();
                    });

            internalInitialize();
        }
        promise.resolve(true);
    }

    @ReactMethod
    public void initialize(String consumerKey, String consumerSecret, Promise promise) {
        Application application = (Application) reactContext.getApplicationContext();

        if (null == VoxeetSDK.instance()) {
            VoxeetSDK.setApplication(application);
            VoxeetSDK.initialize(consumerKey, consumerSecret);

            internalInitialize();
        }

        promise.resolve(true);
    }

    private void internalInitialize() {
        Application application = (Application) reactContext.getApplicationContext();
        VoxeetSDK.conference().ConferenceConfigurations.TelecomWaitingForParticipantTimeout = 30 * 1000; //30s

        //reset the incoming call activity, in case the SDK was no initialized, it would have
        //erased this method call
        //VoxeetPreferences.setDefaultActivity(RNIncomingCallActivity.class.getCanonicalName());

        initNotificationCenter();

        VoxeetToolkit
                .initialize(application, EventBus.getDefault())
                .enableOverlay(true);

        VoxeetSDK.instance().register(this);
    }

    //TODO create a RNINcomingNotification to start a proxy activity - same implementation as Cordova once it's fixed
    public static void initNotificationCenter() {
        //set Android Q as the minimum version no more supported by the full screen mode
        NotificationCenter.instance.register(NotificationMode.FULLSCREEN_INCOMING_CALL, new VersionFilter(VersionFilter.ALL, 29))
                //register notification only mode
                .register(NotificationMode.OVERHEAD_INCOMING_CALL, new IncomingNotification())
                //register full screen mode
                .register(NotificationMode.FULLSCREEN_INCOMING_CALL, new IncomingFullScreen(RNIncomingCallActivity.class))
                //activate fullscreen -> notification mode only
                .setEnforcedNotificationMode(EnforcedNotificationMode.MIXED_INCOMING_CALL);

        try {
            VoxeetSDK.notification().subscribe(new SubscribeInvitation()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void onAccessTokenOk(final String accessToken,
                                final Promise promise) {
        lock(lockAwaitingToken);
        for (TokenCallback callback : mAwaitingTokenCallback) {
            try {
                callback.ok(accessToken);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        mAwaitingTokenCallback.clear();
        unlock(lockAwaitingToken);
        promise.resolve(true);
    }

    @ReactMethod
    public void onAccessTokenKo(final String reason,
                                final Promise promise) {
        try {
            throw new Exception("refreshToken failed with reason := " + reason);
        } catch (Exception e) {
            lock(lockAwaitingToken);
            for (TokenCallback callback : mAwaitingTokenCallback) {
                try {
                    callback.error(e);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            unlock(lockAwaitingToken);
        }
        mAwaitingTokenCallback.clear();
        promise.resolve(true);
    }

    private void postRefreshAccessToken() {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("refreshToken", null);
        /*Log.d("VoxeetCordova", "postRefreshAccessToken: sending call to javascript to refresh token");
        if(null != refreshAccessTokenCallbackInstance) {
            refreshAccessTokenCallbackInstance.invoke();
        }*/
    }

    @ReactMethod
    public void checkForAwaitingConference(Promise promise) {
        SessionService sessionService = VoxeetSDK.session();
        if (null == sessionService) {
            promise.reject(ERROR_SDK_NOT_INITIALIZED);
        } else {
            RNIncomingBundleChecker checker = RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE;
            if (null != checker && checker.isBundleValid()) {
                if (sessionService.isSocketOpen()) {
                    checker.onAccept();
                    RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE = null;
                    promise.resolve(true);
                } else {
                    promise.reject(ERROR_SDK_NOT_LOGGED_IN);
                }
            } else {
                promise.resolve(true);
            }
        }
    }

    @ReactMethod
    public void connect(ReadableMap userInfo, final Promise promise) {
        openSession(userInfo, promise);
    }

    @ReactMethod
    public void openSession(ReadableMap userInfo, final Promise promise) {
        final ParticipantInfo info = toUserInfo(userInfo);

        if (isConnected() && isSameUser(info)) {
            checkForIncomingConference();
            promise.resolve(true);
            return;
        }
        VoxeetSDK.session()
                .open(info)
                .then(result -> {
                    _current_user = info;
                    promise.resolve(result);
                    checkForIncomingConference();
                })
                .error(error -> {
                    promise.reject(error);
                    cancelIncomingConference();
                });
    }

    @ReactMethod
    public void disconnect(final Promise promise) {
        VoxeetSDK.session()
                .close()
                .then(result -> {
                    _current_user = null;
                    promise.resolve(result);
                })
                .error(error -> {
                    _current_user = null;
                    promise.reject(error);
                });
    }

    @ReactMethod
    public void create(@Nullable ReadableMap options, @NonNull final Promise promise) {
        String conferenceId = null;
        ReadableMap params;
        MetadataHolder holder = new MetadataHolder();
        ParamsHolder paramsHolder = new ParamsHolder();

        if (null != options) {

            if (options.hasKey("alias"))
                conferenceId = options.getString("alias");
            if (options.hasKey("params")) {
                params = options.getMap("params");

                if (null != params) {
                    if (valid(params, "videoCodec"))
                        paramsHolder.setVideoCodec(params.getString("videoCodec"));

                    if (valid(params, "ttl"))
                        paramsHolder.putValue("ttl", getInteger(params, "ttl"));

                    if (valid(params, "rtcpMode"))
                        paramsHolder.putValue("rtcpMode", getString(params, "rtcpMode"));

                    if (valid(params, "mode"))
                        paramsHolder.putValue("mode", getString(params, "mode"));

                    if (valid(params, "liveRecording"))
                        paramsHolder.putValue("liveRecording", getString(params, "liveRecording"));
                }
            }

        }

        VoxeetSDK.conference()
                .create(new ConferenceCreateOptions.Builder()
                        .setConferenceAlias(conferenceId)
                        .setMetadataHolder(holder)
                        .setParamsHolder(paramsHolder).build()
                )
                .then(result -> {
                    promise.resolve(ConferenceUtil.toMap(result));
                })
                .error(promise::reject);
    }

    @ReactMethod
    public void join(String conferenceId, @Nullable ReadableMap map, final Promise promise) {
        //for now listener mode also needs microphone...
        boolean listener = false;

        //TODO check for direct api call in react native to add listener in it
        if (!Validate.hasMicrophonePermissions(reactContext)) {
            Log.d(TAG, "join: " + getActivity()+" does not have mic permission");
            if (null != getActivity()) {
                sWaitingHolder = new WaitingJoinHolder(this, conferenceId, map, promise);
                requestMicrophone();
                return;
            } else {
                Log.d(TAG, "join: UNABLE TO REQUEST PERMISSION -- DID YOU REGISTER THE ACTIVITY ?");
            }
        }

        if (null != map && valid(map, "user")) {
            ReadableMap user = getMap(map, "user");
            listener = null != user && "listener".equals(getString(user, "type"));
        }

        VoxeetToolkit.instance().enable(VoxeetToolkit.instance().getConferenceToolkit());

        Conference expected_conference = VoxeetSDK.conference().getConference(conferenceId);

        if(null == expected_conference) {
            promise.reject("-1", "Invalid conference, check the conferenceId used");
            return;
        }

        //TODO when the SDK will be using join parameters, use it
        Log.d(TAG, "join: joining as listener ? " + listener);
        if (!listener) {
            VoxeetSDK.conference()
                    .join(expected_conference)
                    .then(conference -> {
                        promise.resolve(ConferenceUtil.toMap(conference));

                        checkStartVideo();
                    })
                    .error(promise::reject);
        } else {
            VoxeetSDK.conference()
                    .listen(expected_conference)
                    .then(conference -> {
                        promise.resolve(ConferenceUtil.toMap(conference));
                    })
                    .error(promise::reject);
        }
    }

    @ReactMethod
    public void leave(final Promise promise) {
        VoxeetSDK.conference()
                .leave()
                .then(promise::resolve)
                .error(promise::reject);
    }

    @ReactMethod
    public void invite(String conferenceId, ReadableArray participants, final Promise promise) {
        //TODO expose in the SDK the ability to use the conferenceId
        Log.d(TAG, "invite: WARNING :: the provided conferenceId is not yet managed, please make sure you have joined the conference before trying to invite users");

        List<ParticipantInfo> users = null;

        if (null != participants) {
            users = toUserInfos(participants);
        }

        VoxeetSDK.conference()
                .invite(conferenceId, users)
                .then(participants1 -> {
                    promise.resolve(true);
                })
                .error(promise::reject);

    }

    @ReactMethod
    public void sendBroadcastMessage(String message, final Promise promise) {
        String conferenceId = VoxeetSDK.conference().getConferenceId();

        VoxeetSDK.command().send(conferenceId, message)
                .then(promise::resolve)
                .error(promise::reject);
    }

    @ReactMethod
    public void setAudio3DEnabled(boolean enabled) {
        VoxeetSDK.mediaDevice().setAudio3DEnabled(enabled);
    }

    @ReactMethod
    public void setTelecomMode(boolean enabled) {
        VoxeetSDK.conference().ConferenceConfigurations.telecomMode = enabled;
    }

    @ReactMethod
    public void isAudio3DEnabled(Promise promise) {
        promise.resolve(VoxeetSDK.mediaDevice().isAudio3DEnabled());
    }

    @ReactMethod
    public void isTelecomMode(Promise promise) {
        promise.resolve(VoxeetSDK.conference().ConferenceConfigurations.telecomMode);
    }

    @ReactMethod
    public void appearMaximized(boolean activate) {
        VoxeetToolkit.instance().getConferenceToolkit().setDefaultOverlayState(activate ?
                OverlayState.EXPANDED : OverlayState.MINIMIZED);
    }

    @ReactMethod
    public void screenAutoLock(boolean activate) {
        Log.d(TAG, "screenAutoLock: warning, method not implemented");
    }

    @ReactMethod
    public void defaultBuiltInSpeaker(boolean activate) {
        VoxeetSDK.conference().ConferenceConfigurations.isDefaultOnSpeaker = activate;
    }

    @ReactMethod
    public void defaultVideo(boolean activate) {
        startVideo = activate;
    }

    @ReactMethod
    public void isUserLoggedIn(Promise promise) {
        promise.resolve(VoxeetSDK.session().isSocketOpen());
    }

    @ReactMethod
    public void startConference(String conferenceAlias /* alias...*/,
                                ReadableArray participants,
                                final Promise promise) {

        //TODO check for direct api call in react native to add listener in it
        if (!Validate.hasMicrophonePermissions(reactContext)) {
            Log.d(TAG, "join: NOT PERMISSION 2 " + getActivity());
            if (null != getActivity()) {
                sWaitingHolder = new WaitingStartConferenceHolder(this, conferenceAlias, participants, promise);
                requestMicrophone();
                return;
            } else {
                Log.d(TAG, "join: UNABLE TO REQUEST PERMISSION -- DID YOU REGISTER THE ACTIVITY ?");
            }
        }

        VoxeetToolkit.instance().enable(ConferenceToolkitController.class);
        VoxeetSDK.conference()
                .join(conferenceAlias)
                .then(conference -> {
                    Log.d(TAG, "onCall: conference joined");
                    checkStartVideo();
                    promise.resolve(true);
                })
                .error(error -> {
                    error.printStackTrace();
                    promise.reject(error);
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketStateChangeEvent event) {
        switch (event.state) {
            case CLOSING:
            case CLOSED:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionRefusedEvent event) {
        if (null != event.getPermission()) {
            switch (event.getPermission()) {
                case CAMERA:
                    //Validate.requestMandatoryPermissions(VoxeetToolkit.getInstance().getCurrentActivity(),
                    //        new String[]{Manifest.permission.CAMERA},
                    //        PermissionRefusedEvent.RESULT_CAMERA);
                    Activity activity = getCurrentActivity();
                    if (Build.VERSION.SDK_INT >= 23 && null != activity) {
                        activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                PermissionRefusedEvent.RESULT_CAMERA
                        );
                    }
                    break;
            }
        }
    }

    private List<ParticipantInfo> toUserInfos(ReadableArray array) {
        List<ParticipantInfo> result = new ArrayList<>();
        int index = 0;

        while (index < array.size()) {
            if (!array.isNull(index)) {
                ReadableMap map = array.getMap(index);

                result.add(toUserInfo(map));
            }
            index++;
        }
        return result;
    }

    private ParticipantInfo toUserInfo(ReadableMap map) {
        return new ParticipantInfo(map.getString("name"),
                map.getString("externalId"),
                map.getString("avatarUrl"));
    }

    private void checkStartVideo() {
        ConferenceService conferenceService = VoxeetSDK.conference();
        if (startVideo && null != conferenceService) {
            conferenceService
                    .startVideo()
                    .then(result -> {
                        Log.d(TAG, "startVideo " + result);
                    })
                    .error(Throwable::printStackTrace);
        }
    }


    public ParticipantInfo getCurrentUser() {
        return _current_user;
    }

    private boolean isConnected() {
        SessionService sessionService = VoxeetSDK.session();
        return null != sessionService && sessionService.isSocketOpen();
    }

    private boolean isSameUser(@NonNull ParticipantInfo userInfo) {
        return userInfo.getExternalId().equals(getCurrentUser());
    }

    private boolean checkForIncomingConference() {
        RNIncomingBundleChecker checker = RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE;
        if (null != checker && checker.isBundleValid()) {
            if (VoxeetSDK.session().isSocketOpen()) {
                checker.onAccept();
                RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE = null;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void cancelIncomingConference() {
        RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE = null;
    }


    private void lock(ReentrantLock lock) {
        try {
            lock.lock();
        } catch (Exception e) {

        }
    }

    private void unlock(ReentrantLock lock) {
        try {
            if (lock.isLocked())
                lock.unlock();
        } catch (Exception e) {

        }
    }

    private Activity getActivity() {
        if (null != sActivity) return sActivity;
        return mRootViewProvider.getCurrentActivity();
    }

    public static void registerActivity(@NonNull RNVoxeetActivity activity) {
        Log.d(TAG, "registerActivity: sActivity := " + sActivity);
        sActivity = activity;
    }

    public static boolean isWaiting() {
        return null != sWaitingHolder && null != sWaitingHolder.getPromise();
    }

    @Nullable
    public static WaitingAbstractHolder getWaitingJoinHolder() {
        return sWaitingHolder;
    }

    private void requestMicrophone() {
        Log.d(TAG, "requestMicrophone: " + getActivity());
        Validate.requestMandatoryPermissions(getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                PermissionRefusedEvent.RESULT_MICROPHONE);
    }

    private int getInteger(@NonNull ReadableMap map, @NonNull String key) {
        try {
            return map.hasKey(key) ? map.getInt(key) : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean getBoolean(@NonNull ReadableMap map, @NonNull String key) {
        try {
            return map.hasKey(key) && map.getBoolean(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    private ReadableMap getMap(@NonNull ReadableMap map, @NonNull String key) {
        try {
            return map.hasKey(key) ? map.getMap(key) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private String getString(@NonNull ReadableMap map, @NonNull String key) {
        try {
            if (map.hasKey(key)) return map.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean valid(@NonNull ReadableMap map, @NonNull String key) {
        try {
            return map.hasKey(key) && !map.isNull(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
