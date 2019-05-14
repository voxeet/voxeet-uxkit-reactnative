
package com.voxeet;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
import com.voxeet.push.firebase.FirebaseController;
import com.voxeet.sdk.core.VoxeetEnvironmentHolder;
import com.voxeet.sdk.core.VoxeetSdk;
import com.voxeet.sdk.core.preferences.VoxeetPreferences;
import com.voxeet.sdk.core.services.authenticate.token.RefreshTokenCallback;
import com.voxeet.sdk.core.services.authenticate.token.TokenCallback;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.success.ConferenceRefreshedEvent;
import com.voxeet.sdk.events.success.SocketConnectEvent;
import com.voxeet.sdk.events.success.SocketStateChangeEvent;
import com.voxeet.sdk.json.UserInfo;
import com.voxeet.sdk.json.internal.MetadataHolder;
import com.voxeet.sdk.json.internal.ParamsHolder;
import com.voxeet.sdk.models.ConferenceResponse;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.specifics.RNRootViewProvider;
import com.voxeet.specifics.RNVoxeetActivity;
import com.voxeet.specifics.waiting.WaitingAbstractHolder;
import com.voxeet.specifics.waiting.WaitingJoinHolder;
import com.voxeet.specifics.waiting.WaitingStartConferenceHolder;
import com.voxeet.toolkit.controllers.VoxeetToolkit;
import com.voxeet.toolkit.implementation.overlays.OverlayState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;

public class RNVoxeetConferencekitModule extends ReactContextBaseJavaModule {

    private static final String ERROR_SDK_NOT_INITIALIZED = "ERROR_SDK_NOT_INITIALIZED";
    private static final String ERROR_SDK_NOT_LOGGED_IN = "ERROR_SDK_NOT_LOGGED_IN";

    private final static String TAG = RNVoxeetConferencekitModule.class.getSimpleName();

    public static boolean startVideo;

    @Nullable
    private static RNVoxeetActivity sActivity;

    private final ReactApplicationContext reactContext;
    private final RNRootViewProvider mRootViewProvider;
    private UserInfo _current_user;
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

        if (null == VoxeetSdk.getInstance()) {
            //refreshAccessTokenCallbackInstance = callback;

            VoxeetSdk.initialize(application,
                    accessToken,
                    new RefreshTokenCallback() {
                        @Override
                        public void onRequired(TokenCallback callback) {
                            lock(lockAwaitingToken);
                            if (!mAwaitingTokenCallback.contains(callback)) {
                                mAwaitingTokenCallback.add(callback);
                            }
                            unlock(lockAwaitingToken);
                            postRefreshAccessToken();
                        }
                    }, null);

            internalInitialize();
            //callback.invoke();
        }
        promise.resolve(true);
    }

    @ReactMethod
    public void initialize(String consumerKey, String consumerSecret, Promise promise) {
        Application application = (Application) reactContext.getApplicationContext();

        if (null == VoxeetSdk.getInstance()) {
            VoxeetSdk.initialize(application,
                    consumerKey, consumerSecret, null);

            internalInitialize();
        }

        promise.resolve(true);
    }

    private void internalInitialize() {
        Application application = (Application) reactContext.getApplicationContext();
        VoxeetSdk.getInstance().getConferenceService().setTimeOut(30 * 1000); //30s

        //also enable the push token upload and log
        FirebaseController.getInstance()
                .log(true)
                .enable(true)
                .createNotificationChannel(application);

        //reset the incoming call activity, in case the SDK was no initialized, it would have
        //erased this method call
        VoxeetPreferences.setDefaultActivity(RNIncomingCallActivity.class.getCanonicalName());

        VoxeetToolkit
                .initialize(application, EventBus.getDefault())
                .enableOverlay(true);

        VoxeetSdk.getInstance().register(application, this);
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
        if (null == VoxeetSdk.getInstance()) {
            promise.reject(ERROR_SDK_NOT_INITIALIZED);
        } else {
            RNIncomingBundleChecker checker = RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE;
            if (null != checker && checker.isBundleValid()) {
                if (VoxeetSdk.getInstance().isSocketOpen()) {
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
        final UserInfo info = toUserInfo(userInfo);

        if (isConnected() && isSameUser(info)) {
            checkForIncomingConference();
            promise.resolve(true);
            return;
        }
        VoxeetSdk.getInstance()
                .logUserWithChain(info)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                        _current_user = info;
                        promise.resolve(result);
                        checkForIncomingConference();
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        promise.reject(error);
                        cancelIncomingConference();
                    }
                });
    }

    @ReactMethod
    public void disconnect(final Promise promise) {
        VoxeetSdk.getInstance()
                .logout()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                        _current_user = null;
                        promise.resolve(result);
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        _current_user = null;
                        promise.reject(error);
                    }
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

        VoxeetSdk.getInstance().getConferenceService()
                .create(conferenceId, holder, paramsHolder)
                .then(new PromiseExec<ConferenceResponse, Object>() {
                    @Override
                    public void onCall(@Nullable ConferenceResponse result, @NonNull Solver<Object> solver) {
                        promise.resolve(ConferenceUtil.toMap(result));
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        promise.reject(error);
                    }
                });
    }

    @ReactMethod
    public void join(String conferenceId, @Nullable ReadableMap map, final Promise promise) {
        //for now listener mode also needs microphone...
        boolean listener = false;

        //TODO check for direct api call in react native to add listener in it
        if (!Validate.hasMicrophonePermissions(reactContext)) {
            Log.d(TAG, "join: NOT PERMISSION 1 " + getActivity());
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

        VoxeetToolkit.getInstance().enable(VoxeetToolkit.getInstance().getConferenceToolkit());

        //TODO when the SDK will be using join parameters, use it
        Log.d(TAG, "join: joining as listener ? " + listener);
        if (!listener) {
            VoxeetSdk.getInstance().getConferenceService()
                    .join(conferenceId)
                    .then(new PromiseExec<Boolean, Object>() {
                        @Override
                        public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                            promise.resolve(result);

                            checkStartVideo();
                        }
                    })
                    .error(new ErrorPromise() {
                        @Override
                        public void onError(@NonNull Throwable error) {
                            promise.reject(error);
                        }
                    });
        } else {
            VoxeetSdk.getInstance().getConferenceService()
                    .listenConference(conferenceId)
                    .then(new PromiseExec<Boolean, Object>() {
                        @Override
                        public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                            promise.resolve(result);
                        }
                    })
                    .error(new ErrorPromise() {
                        @Override
                        public void onError(@NonNull Throwable error) {
                            promise.reject(error);
                        }
                    });
        }
    }

    @ReactMethod
    public void leave(final Promise promise) {
        VoxeetSdk.getInstance().getConferenceService()
                .leave()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                        promise.resolve(result);
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        promise.reject(error);
                    }
                });
    }

    @ReactMethod
    public void invite(String conferenceId, ReadableArray participants, final Promise promise) {
        //TODO expose in the SDK the ability to use the conferenceId
        Log.d(TAG, "invite: WARNING :: the provided conferenceId is not yet managed, please make sure you have joined the conference before trying to invite users");

        List<UserInfo> users = null;

        if (null != participants) {
            users = toUserInfos(participants);
        }

        VoxeetSdk.getInstance().getConferenceService()
                .inviteUserInfos(conferenceId, users)
                .then(new PromiseExec<List<ConferenceRefreshedEvent>, Object>() {
                    @Override
                    public void onCall(@Nullable List<ConferenceRefreshedEvent> result, @NonNull Solver<Object> solver) {
                        promise.resolve(true);
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        promise.reject(error);
                    }
                });

    }

    @ReactMethod
    public void sendBroadcastMessage(String message, final Promise promise) {
        String conferenceId = VoxeetSdk.getInstance().getConferenceService().getConferenceId();

        VoxeetSdk.getInstance().getConferenceService().sendMessage(conferenceId, message)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                        promise.resolve(result);
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        promise.reject(error);
                    }
                });
    }

    @ReactMethod
    public void appearMaximized(boolean activate) {
        VoxeetToolkit.getInstance().getConferenceToolkit().setDefaultOverlayState(activate ?
                OverlayState.EXPANDED : OverlayState.MINIMIZED);
    }

    @ReactMethod
    public void screenAutoLock(boolean activate) {
        Log.d(TAG, "screenAutoLock: warning, method not implemented");
    }

    @ReactMethod
    public void defaultBuiltInSpeaker(boolean activate) {
        VoxeetSdk.getInstance().getConferenceService().setDefaultBuiltInSpeaker(activate);
    }

    @ReactMethod
    public void defaultVideo(boolean activate) {
        startVideo = activate;
    }

    @ReactMethod
    public void isUserLoggedIn(Promise promise) {
        promise.resolve(VoxeetSdk.getInstance().isSocketOpen());
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

        VoxeetToolkit.getInstance().getConferenceToolkit()
                .join(conferenceAlias, null, null, null)
                .then(new PromiseExec<Boolean, Boolean>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<Boolean> solver) {
                        Log.d(TAG, "onCall: conference joined");
                        checkStartVideo();
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        error.printStackTrace();
                        promise.reject(error);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final SocketConnectEvent event) {
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketStateChangeEvent event) {
        switch (event.message()) {
            case "CLOSING":
            case "CLOSED":
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

    private List<UserInfo> toUserInfos(ReadableArray array) {
        List<UserInfo> result = new ArrayList<>();
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

    private UserInfo toUserInfo(ReadableMap map) {
        return new UserInfo(map.getString("name"),
                map.getString("externalId"),
                map.getString("avatarUrl"));
    }

    private void checkStartVideo() {
        if (startVideo) {
            VoxeetSdk.getInstance().getConferenceService()
                    .startVideo()
                    .then(new PromiseExec<Boolean, Object>() {
                        @Override
                        public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {

                        }
                    })
                    .error(new ErrorPromise() {
                        @Override
                        public void onError(@NonNull Throwable error) {

                        }
                    });
        }
    }


    public UserInfo getCurrentUser() {
        return _current_user;
    }

    private boolean isConnected() {
        return VoxeetSdk.getInstance() != null
                && VoxeetSdk.getInstance().isSocketOpen();
    }

    private boolean isSameUser(@NonNull UserInfo userInfo) {
        return userInfo.getExternalId().equals(getCurrentUser());
    }

    private boolean checkForIncomingConference() {
        RNIncomingBundleChecker checker = RNIncomingCallActivity.REACT_NATIVE_ROOT_BUNDLE;
        if (null != checker && checker.isBundleValid()) {
            if (VoxeetSdk.getInstance().isSocketOpen()) {
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
