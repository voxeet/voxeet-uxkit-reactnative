# Voxeet UXKit React Native

## SDK License agreement

Before using the react-native plugin, please review and accept the [Dolby Software License Agreement](SDK_LICENSE.md).

## Getting started

Use the following commands to install and configure the UXKit on React Native:

```bash
npm install @voxeet/react-native-voxeet-conferencekit --save
npx react-native link @voxeet/react-native-voxeet-conferencekit
```

> Note: for iOS & Android, you must perform some mandatory modifications to your project.

## Mandatory modifications

### iOS (react-native >= 0.60)

1. Open your Xcode workspace from YOUR_PROJECT/ios/YOUR_PROJECT.xcworkspace

2. Go to your target settings -> 'Signing & Capabilities' -> '+ Capability' -> 'Background Modes'
    - Turn on 'Audio, AirPlay and Picture in Picture'  
    - Turn on 'Voice over IP'

    If you want to support CallKit (receiving incoming call when application is killed) with VoIP push notification, enable 'Push Notifications' (you will need to upload your [VoIP push certificate](https://developer.apple.com/account/ios/certificate/) to the [Dolby.io Dashboard](https://dolby.io/dashboard/)).

3. Privacy **permissions**, add two new keys in the Info.plist:
    - Privacy - Microphone Usage Description
    - Privacy - Camera Usage Description

4. Open a terminal and go to YOUR_PROJECT/ios
    ```bash
    pod install
    ```

5. Open your .xcworkspace project, select Product > Scheme > Edit scheme > Build > Uncheck "Parallelize Build".

### Android

> Note: to enable Firebase on Android (for Push Notification), please add the [react-native-voxeet-firebase](https://github.com/voxeet/react-native-voxeet-firebase) library.

1. In `build.gradle`, add the maven repository and set the `minSdkVersion` to at least 21.
    ```gradle
    allprojects {
        repositories {
            maven { url "https://android-sdk.voxeet.com/release" }
        }
    }
    ```

2. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```gradle
    implementation (project(':@voxeet_react-native-voxeet-conferencekit')) {
        transitive = true
    }
  	```

    **Warning: the SDK is only compatible with the Hermes engine**

    ```gradle
    project.ext.react = [
        enableHermes: true,  // clean and rebuild if changing
    ]
    ```

    A pickFirst option must be used for the libc++ shared object:

    ```gradle
    android {
        packagingOptions {
            pickFirst '**/armeabi-v7a/libc++_shared.so'
            pickFirst '**/x86/libc++_shared.so'
            pickFirst '**/arm64-v8a/libc++_shared.so'
            pickFirst '**/x86_64/libc++_shared.so'
        }
    }
    ```

3. Open up `android/app/src/main/java/[...]/MainActivity.java`
    
    If you are using `Expo` you can skip this step.
    
    If your `MainActivity` extends `ReactActivity`, change from `MainActivity extends ReactActivity` to `MainActivity extends RNVoxeetActivity`. With the following import: `import com.voxeet.specifics.RNVoxeetActivity`

4. Update the `app/src/main/AndroidManifest.xml` file with the following permissions:

    ```xml
    <!-- VOXEET PERMISSIONS - WARNING: THERE MAY BE DUPLICATES - no expected issues -->

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>

    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.INTERACT_ACROSS_USERS_FULL" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
    ```


## Usage

```javascript
import { VoxeetSDK } from "@voxeet/react-native-voxeet-conferencekit";
```

## Configuration

Depending on your environment, you must configure your project according to the public documentation, [Voxeet UXKit iOS](https://github.com/voxeet/voxeet-uxkit-ios) and [Voxeet UXKit Android](https://github.com/voxeet/voxeet-uxkit-android).

## Local build

To build locally the TypeScript definition, run the following command:

```bash
npm run build-library
```

The typescript command line needs local dev resolutions (available in the `package.json`)

```bash
npm i -D @types/react ...
```
