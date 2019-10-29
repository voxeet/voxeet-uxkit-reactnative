
package com.voxeet;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.voxeet.rn.manifests.RNVoxeetManifestComponent;
import com.voxeet.video.RNVideoViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RNVoxeetConferencekitPackage implements ReactPackage {

    public RNVoxeetConferencekitPackage() {
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new RNVoxeetConferencekitModule(RNVoxeetManifestComponent.root_view_provider,
                reactContext));
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.<ViewManager>singletonList(
                new RNVideoViewManager()
        );
    }
}