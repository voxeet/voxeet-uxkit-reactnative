package com.voxeet.reactnative.utils;

import android.Manifest;
import android.os.Build;
import android.util.Log;

import com.voxeet.promise.Promise;
import com.voxeet.uxkit.common.permissions.PermissionController;
import com.voxeet.uxkit.common.permissions.PermissionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RNPermissionHelper {

    private final static List<String> PERMISSIONS = Arrays.asList(Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA);

    public static Promise<Boolean> requestDefaultPermission() {
        return new Promise<>(solver -> {
            List<String> permissions = new ArrayList<>(PERMISSIONS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            PermissionController.requestPermissions(permissions).then(permissionResults -> {
                for (PermissionResult res : permissionResults) {
                    if (Manifest.permission.RECORD_AUDIO.equals(res.permission) && !res.isGranted) {
                        solver.resolve(false);
                        return;
                    }
                }
                solver.resolve(true);
            }).error(solver::reject);
        });
    }
}
