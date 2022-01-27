package com.voxeet.reactnative.specifics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.ReactActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.voxeet.audio.utils.__Call;
import com.voxeet.reactnative.RNVoxeetConferencekitModule;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.uxkit.common.activity.IPermissionContractHolder;
import com.voxeet.uxkit.common.activity.VoxeetCommonAppCompatActivityWrapper;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;
import com.voxeet.uxkit.common.activity.bundle.IncomingBundleChecker;
import com.voxeet.uxkit.common.permissions.IRequestPermissions;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.service.VoxeetSystemService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RNVoxeetActivity extends ReactActivity {

    private VoxeetCommonAppCompatActivityWrapper<VoxeetSystemService> mActivityObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityObject = new VoxeetCommonAppCompatActivityWrapper<VoxeetSystemService>(this) {
            @Override
            protected void onSdkServiceAvailable() {

            }

            @Override
            protected void onConferenceState(@NonNull ConferenceStatusUpdatedEvent conferenceStatusUpdatedEvent) {

            }

            @Override
            protected boolean canBeRegisteredToReceiveCalls() {
                return true;
            }

            @Override
            public IncomingBundleChecker createIncomingBundleChecker(@Nullable Intent intent) {
                return new DefaultIncomingBundleChecker(intent, null);
            }

            @Override
            public IPermissionContractHolder createPermissionContractHolder() {
                return new PermissionContractHolder();
            }
        };
        mActivityObject.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mActivityObject.onResume();
        RNVoxeetConferencekitModule.registerActivity(this);

        if (null != VoxeetToolkit.instance()) {
            //to prevent uninitialized toolkit but ... it's highly recommended for future releases to always init
            VoxeetToolkit.instance().getConferenceToolkit().forceReattach();
        }
    }

    @Override
    protected void onPause() {
        //no need to unregister, it's not having multiple activities
        mActivityObject.onPause();

        super.onPause();
    }

    public void onInvitationBundle(Intent intent) {
        mActivityObject.onNewIntent(intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mActivityObject.onNewIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivityObject.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        super.requestPermissions(permissions, requestCode, listener);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class PermissionContractHolder implements IPermissionContractHolder {

        private Map<String, Boolean> permissionRequestResults = new HashMap<>();

        private IRequestPermissions requestPermissions = new IRequestPermissions() {
            @Override
            public void requestPermissions(@NonNull List<String> list, @NonNull __Call<Map<String, Boolean>> call) {
                RNVoxeetActivity.this.requestPermissions(list.toArray(new String[0]), 10, (requestCode, permissions, grantResults) -> {
                    if(requestCode != 10) return false;

                    Map<String, Boolean> grants = new HashMap<>();
                    int i = 0;
                    while (i < permissions.length) {
                        grants.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
                        i++;
                    }
                    call.apply(grants);
                    return true;
                });
            }

            @Override
            public boolean hasPermission(@NonNull String permission) {
                return ContextCompat.checkSelfPermission(RNVoxeetActivity.this, permission)
                    == PackageManager.PERMISSION_GRANTED;
            }

            @Override
            public boolean isPermissionNeverAskAgain(@NonNull String s) {
                return PermissionContractHolder.this.isPermissionNeverAskAgain(s);
            }

            @Override
            public boolean shouldShowRequestPermissionRationale(@NonNull String s) {
                return PermissionContractHolder.this.shouldShowRequestPermissionRationale(s);
            }
        };

        @Override
        public boolean isPermissionNeverAskAgain(@NonNull String permission) {
            if (!permissionRequestResults.containsKey(permission)) return false;
            Boolean granted = permissionRequestResults.get(permission);
            if (null == granted) granted = false;
            return !granted && !shouldShowRequestPermissionRationale(permission);
        }

        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return RNVoxeetActivity.this.shouldShowRequestPermissionRationale(permission);
            }
            return false;
        }

        @Override
        public IRequestPermissions getRequestPermissions() {
            return requestPermissions;
        }
    }
}
