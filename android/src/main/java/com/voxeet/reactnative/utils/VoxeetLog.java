package com.voxeet.reactnative.utils;

import android.util.Log;

import androidx.annotation.NonNull;

public class VoxeetLog {
    public static void log(@NonNull String tag, @NonNull String text) {
        Log.d("VoxeetLog", ":: " + tag + " // " + text);
    }
}
