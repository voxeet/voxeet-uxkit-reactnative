package com.voxeet.events;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.WritableMap;

/**
 * Created by kevinleperf on 11/11/2018.
 */

public abstract class EventFormatterCallback<TYPE> {

    private String name;
    private Class<TYPE> klass;

    protected EventFormatterCallback(Class<TYPE> klass) {
        name = klass.getSimpleName();
        this.klass = klass;
    }

    @NonNull
    String name() {
        return name;
    }

    @NonNull
    Class<TYPE> getKlass() {
        return klass;
    }

    abstract void transform(@NonNull WritableMap map, @NonNull TYPE instance);
}
