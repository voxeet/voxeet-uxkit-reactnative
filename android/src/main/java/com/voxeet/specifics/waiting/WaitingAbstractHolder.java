package com.voxeet.specifics.waiting;

import com.facebook.react.bridge.Promise;
import com.voxeet.RNVoxeetConferencekitModule;

/**
 * Created by kevinleperf on 20/12/2018.
 */

public abstract class WaitingAbstractHolder {
    protected RNVoxeetConferencekitModule module;
    protected Promise promise;

    protected WaitingAbstractHolder(RNVoxeetConferencekitModule module,
                                    Promise promise) {
        this.module = module;
        this.promise = promise;
    }


    public Promise getPromise() {
        return promise;
    }

    public abstract void rejoin();
}
