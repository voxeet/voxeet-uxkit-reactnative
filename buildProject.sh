#!/bin/bash

# cd to the library files
cd lib

#run typescript compiler
tsc

#back to main
cd ..

#cp proper files into the dist folder
cp ./package.json ./build/dist/
cp ./build_ios_frameworks.sh ./build/dist/build_ios_frameworks.sh

#copy android
mkdir -p build/dist/android
cp -r android/src build/dist/android/src
cp android/build.gradle build/dist/android/build.gradle

#copy ios
mkdir -p build/dist/ios
cp ios/Cartfile build/dist/ios/Cartfile
cp -r ios/RNVoxeetConferencekit build/dist/ios/RNVoxeetConferencekit
cp -r ios/RNVoxeetConferencekit.xcodeproj build/dist/ios/RNVoxeetConferencekit.xcodeproj