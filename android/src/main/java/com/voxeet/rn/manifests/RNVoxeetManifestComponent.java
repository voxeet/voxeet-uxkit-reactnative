package com.voxeet.rn.manifests;

import android.app.Application;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.RNVoxeetConferencekitModule;
import com.voxeet.VoxeetSDK;
import com.voxeet.sdk.manifests.AbstractManifestComponentProvider;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.utils.VoxeetEnvironmentHolder;
import com.voxeet.specifics.RNRootViewProvider;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;

import org.greenrobot.eventbus.EventBus;

public final class RNVoxeetManifestComponent extends AbstractManifestComponentProvider {

    /**
     * Static instance of the root view provider to be used by the app's voxeet instance
     */
    public static RNRootViewProvider root_view_provider;

    private static final String TAG = RNVoxeetManifestComponent.class.getSimpleName();

    @Override
    protected void init(@NonNull Context context, @Nullable ProviderInfo providerInfo) {
        if (!(context instanceof Application)) {
            Log.d(TAG, "init: ISSUE CONTEXT IS NOT AN Application");
            return;
        }

        Application application = (Application) context;

        //Set the context to the SDK to ensure that all the required components are available
        //(call made here in case that the RNVoxeetManifestComponent class is called before the SDK)
        VoxeetSDK.setApplication(context);

        VoxeetToolkit.initialize(application, EventBus.getDefault());


        //force a default voxeet preferences manager
        //in sdk mode, no issues
        VoxeetPreferences.init(application, new VoxeetEnvironmentHolder(application));

        // set UXKit initialization elements

        RNVoxeetManifestComponent.root_view_provider = new RNRootViewProvider(application, VoxeetToolkit.instance());
        application.registerActivityLifecycleCallbacks(RNVoxeetManifestComponent.root_view_provider);
        VoxeetToolkit.instance().setProvider(RNVoxeetManifestComponent.root_view_provider);

        VoxeetToolkit.instance().enableOverlay(true);

        //change the overlay used by default
        VoxeetToolkit.instance().getConferenceToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
        VoxeetToolkit.instance().getReplayMessageToolkit().setDefaultOverlayState(OverlayState.EXPANDED);

        RNVoxeetConferencekitModule.initNotificationCenter();
    }

    @Override
    protected String getComponentName() {
        return RNVoxeetManifestComponent.class.getSimpleName();
    }

    @Override
    protected String getDefaultAuthority() {
        return "com.voxeet.rn.manifests.";
    }
}
