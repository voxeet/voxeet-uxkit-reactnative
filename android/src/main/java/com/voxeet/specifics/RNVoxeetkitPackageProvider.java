package com.voxeet.specifics;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * Created by kevinleperf on 21/11/2018.
 */

public interface RNVoxeetkitPackageProvider {

    void setCurrentActivity(@NonNull Activity activity);
}
