package com.voxeet.reactnative.specifics;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * Created by kevinleperf on 21/11/2018.
 */

public interface RNVoxeetkitPackageProvider {

    void setCurrentActivity(@NonNull Activity activity);
}
