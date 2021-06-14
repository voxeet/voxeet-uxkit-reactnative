#!/bin/bash

PATH=`ls node_modules/react-native/android/com/facebook/react/react-native/ | grep ^0`

ZIP=node_modules/react-native/android/com/facebook/react/react-native/$PATH/react-native-$PATH.aar

/usr/bin/zip -d $ZIP jni/x86_64/libc++_shared.so
/usr/bin/zip -d $ZIP jni/x86/libc++_shared.so
/usr/bin/zip -d $ZIP jni/armeabi-v7a/libc++_shared.so
/usr/bin/zip -d $ZIP jni/arm64-v8a/libc++_shared.so

echo "Patch done for the outdated libc++ inside React (r21b embedded insides Voxeet's dependencies"
