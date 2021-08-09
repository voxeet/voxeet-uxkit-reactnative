# Voxeet UXKit React Native

## SDK License agreement

Before using the react-native plugin, please review and accept the [Dolby Software License Agreement](SDK_LICENSE.md).

## Getting started

Use the following commands to install and configure the UXKit on React Native:

```bash
npm install @voxeet/react-native-voxeet-conferencekit --save
npx react-native link @voxeet/react-native-voxeet-conferencekit
```

> Note: For iOS and Android, you must perform some mandatory modifications to your project.

## Mandatory modifications

### iOS (react-native >= 0.60)

1. Open your Xcode workspace from YOUR_PROJECT/ios/YOUR_PROJECT.xcworkspace

2. Go to your target settings and select 'Signing & Capabilities', then click '+ Capability', and click 'Background Modes'.
    - Turn on 'Audio, AirPlay and Picture in Picture'  
    - Turn on 'Voice over IP'

    If you want to support CallKit (receiving incoming call when application is killed) with VoIP push notification, enable 'Push Notifications' (you will need to upload your [VoIP push certificate](https://developer.apple.com/account/ios/certificate/) to the [Dolby.io Dashboard](https://dolby.io/dashboard/)).

3. For privacy permissions, add two new keys in the Info.plist:
    - Privacy - Microphone Usage Description
    - Privacy - Camera Usage Description

4. Open a terminal and go to YOUR_PROJECT/ios.
    ```bash
    pod install
    ```
    If you are using react-native 0.64, there is a known bug from the FBReactNativeSpec library. You have to go into your Pods project in Xcode workspace, select FBReactNativeSpec target, and in the "Build Phases" section, drag and drop the "[CP-User] Generate Specs" step just under the "Dependencies" step (2nd position). You have to do this step after every pod install or pod update.

5. Open your .xcworkspace project, navigate to Product > Scheme > Edit scheme > Build > and uncheck "Parallelize Build".

### Android

1. In `android/app/build.gradle`, add the maven repository and set the `minSdkVersion` to at least **21**.

2. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
    **Warning: the SDK is only compatible with the Hermes engine**

    ```gradle
    project.ext.react = [
        enableHermes: true,  // clean and rebuild if changing
    ]
    ```

3. use gradle configuration's pickFirst for the libc++_shared.so in the `android/app/build.gradle` file

    ```
    android {
      compileSdkVersion rootProject.ext.compileSdkVersion //just for reference

      ...

      packagingOptions {
        pickFirst "**/libc++_shared.so"
      }

      ...
    }
    ```

4. Open the `android/app/src/main/java/[...]/MainActivity.java` file: 
    
    If you are using `Expo` you can skip this step.
    
    If your `MainActivity` extends `ReactActivity`, change from `MainActivity extends ReactActivity` to `MainActivity extends RNVoxeetActivity`. With the following import: `import com.voxeet.reactnative.specifics.RNVoxeetActivity`

5. Update the `android/build.gradle` and update the allProjects block with the following :

```gradle
allprojects {
    repositories {
        maven { url "https://android-sdk.voxeet.com/release" }
        maven { url "https://dl.bintray.com/voxeet/maven" }
        ... // keep the other lines below
    }
}
```

6. Note : if crashes occurs in debug mode, remove `debugImplementation` for any *flipper* library from the android/app/build.gradle + remove the java file in your debug/ folder. (the initializeFlipper method from the MainApplication can be removed as well)

## Usage

```javascript
import { VoxeetSDK } from "@voxeet/react-native-voxeet-conferencekit";
```

Note: The VoxeetEvents is now deprecated and will disappear from the library itself.

### Initialization

```
VoxeetSDK.initialize(appKey, appSecret);
```

or 

```
VoxeetSDK.initializeToken(accessToken, () => {
    return new Promise((resolve, reject) => {
        ... //get the new accessToken
        resolve(theNewAccessToken);
    });
});
```

### Open a session (+ check for conference to join)


Once the SDK is initialized, try to connect your current user as soon as possible.

```
await VoxeetSDK.connect(new UserInfo("externalId", "name", "optAvatarUrl"));
```

Once the session is started, if an incoming call is accepted by the user, it is initiated right away.

### Join and leave conferences

Use the corresponding method to perform the action:

```
const conference = await VoxeetSDK.create({ alias: "yourConferenceAlias" });
await VoxeetSDK.join(conference.conferenceId);
```

To leave, use the following command:

```
await VoxeetSDK.leave(conferenceId);
```

### Invite participants


## Events

You can subscribe to events via the `addListener` (and unsubscribe via the corresponding `removeListener`) method in `VoxeetSDK.events`.

### Example

```
import { VoxeetSDK } from "@voxeet/react-native-voxeet-conferencekit";
import { ConferenceStatusUpdatedEvent } from "@voxeet/react-native-voxeet-conferencekit";

const onConferenceStatus = (event: ConferenceStatusUpdatedEvent) => {
  console.warn("event received", event);
}

VoxeetSDK.events.addListener("ConferenceStatusUpdatedEvent", onConferenceStatus);
```

### ConferenceStatusUpdatedEvent

- conferenceId: `string`
- conferenceAlias: `string|undefined`
- status: `ConferenceStatus`

## Configuration

Depending on your environment, you must configure your project according to the public documentation, [Voxeet UXKit iOS](https://github.com/voxeet/voxeet-uxkit-ios) and [Voxeet UXKit Android](https://github.com/voxeet/voxeet-uxkit-android).

## Local build

To build locally the TypeScript definition, run the following command:

```bash
npm run build-library
```

The typescript command line needs local dev resolutions (available in the `package.json`).

```bash
npm i -D @types/react ...
```
