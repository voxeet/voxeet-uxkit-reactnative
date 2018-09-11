
package com.voxeet.rn;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import sdk.voxeet.com.toolkit.main.VoxeetToolkit;
import sdk.voxeet.com.toolkit.views.uitookit.sdk.overlays.OverlayState;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.events.success.ConferenceRefreshedEvent;
import voxeet.com.sdk.events.success.SocketConnectEvent;
import voxeet.com.sdk.events.success.SocketStateChangeEvent;
import voxeet.com.sdk.json.UserInfo;

public class RNReactNativeVoxeetConferencekitModule extends ReactContextBaseJavaModule {

    private final static String TAG = RNReactNativeVoxeetConferencekitModule.class.getSimpleName();

    private final ReactApplicationContext reactContext;
    private Promise _log_in_callback;

    public RNReactNativeVoxeetConferencekitModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNReactNativeVoxeetConferencekit";
    }

    @ReactMethod
    public void initialize(String consumerKey, String consumerSecret) {
        VoxeetSdk.initialize((Application) getReactApplicationContext().getApplicationContext(),
                consumerKey, consumerSecret, null);

        VoxeetToolkit.initialize((Application) getReactApplicationContext().getApplicationContext(), EventBus.getDefault());
    }

    @ReactMethod
    public void openSession(String userId, String name, String avatarUrl, Promise promise) {
        if (null != _log_in_callback) {
            promise.reject("CONNECTING", "Already connecting");
            return;
        }

        VoxeetSdk.getInstance().logUser(new UserInfo(userId, name, avatarUrl));

        _log_in_callback = promise;
    }

    @ReactMethod
    public void appearMaximized(boolean activate) {
        VoxeetToolkit.getInstance().getConferenceToolkit().setDefaultOverlayState(activate ?
                OverlayState.EXPANDED : OverlayState.MINIMIZED);
    }

    @ReactMethod
    public void defaultBuiltInSpeaker(boolean activate) {
        VoxeetSdk.getInstance().getConferenceService().setDefaultBuiltInSpeaker(activate);
    }

    @ReactMethod
    public void screenAutoLock(boolean activate) {
        Log.d(TAG, "screenAutoLock: warning, method not implemented");
    }

    @ReactMethod
    public void startConference(String conferenceId /* alias...*/,
                                ReadableArray participants,
                                boolean invite, final Promise promise) {
        List<UserInfo> users = null;
        if (invite && null != participants) {
            users = toUserInfos(participants);
        }

        final List<UserInfo> finalUsers = users;
        VoxeetToolkit.getInstance().getConferenceToolkit()
                .join(conferenceId, null)
                .then(new PromiseExec<Boolean, List<ConferenceRefreshedEvent>>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<List<ConferenceRefreshedEvent>> solver) {
                        Log.d(TAG, "onCall: conference joined");
                        if (null != finalUsers) {
                            solver.resolve(VoxeetToolkit.getInstance().getConferenceToolkit().invite(finalUsers));
                        } else {
                            solver.resolve((List<ConferenceRefreshedEvent>) null);
                        }
                    }
                })
                .then(new PromiseExec<List<ConferenceRefreshedEvent>, Void>() {
                    @Override
                    public void onCall(@Nullable List<ConferenceRefreshedEvent> result, @NonNull Solver<Void> solver) {
                        Log.d(TAG, "onCall: join conference ok");
                        promise.resolve(true);
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
        if (null != _log_in_callback) {
            _log_in_callback.resolve(true);
            _log_in_callback = null;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketStateChangeEvent event) {
        switch (event.message()) {
            case "CLOSING":
            case "CLOSED":
                if (null != _log_in_callback) {
                    _log_in_callback.resolve(false);
                    _log_in_callback = null;
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
}