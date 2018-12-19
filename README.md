
# react-native-voxeet-conferencekit

## Getting started

`$ npm install react-native-voxeet-conferencekit --save`

### Mostly automatic installation

`$ react-native link react-native-voxeet-conferencekit`

### Manual installation


#### iOS

1. Open your Xcode project from YOUR_PROJECT/ios/YOUR_PROJECT.xcworkspace (or .xcodeproj if there is no .xcworkspace)

2. Go to your target settings -> 'Capabilities' -> 'Background Modes': enable **background mode**
- Turn on 'Audio, AirPlay and Picture in Picture'  
- Turn on 'Voice over IP'

If you want to support CallKit (receiving incoming call when application is killed) with VoIP push notification, enable 'Push Notifications' (you will need to upload your [VoIP push certificate](https://developer.apple.com/account/ios/certificate/) to the Voxeet developer portal).

3. Privacy **permissions**, add two new keys in the Info.plist:
- Privacy - Microphone Usage Description
- Privacy - Camera Usage Description

4. Open a Finder and go to YOUR_PROJECT/node_modules/react-native-voxeet-conferencekit/ios/Carthage/Build/iOS, drag and drop `VoxeetSDK.framework`, `WebRTC.framework`, `VoxeetConferenceKit.framework` and `Kingfisher.framework` into the **Frameworks** folder from Xcode project (deselect `Copy items if needed` and select your target(s))

5. Go to your target settings -> 'Build Phases': Add a **New Run Script Phase**

```bash
/usr/local/bin/carthage copy-frameworks
```

Input files:
- $(PROJECT_DIR)/../node_modules/react-native-voxeet-conferencekit/ios/Carthage/Build/iOS/Kingfisher.framework
- $(PROJECT_DIR)/../node_modules/react-native-voxeet-conferencekit/ios/Carthage/Build/iOS/VoxeetSDK.framework
- $(PROJECT_DIR)/../node_modules/react-native-voxeet-conferencekit/ios/Carthage/Build/iOS/WebRTC.framework
- $(PROJECT_DIR)/../node_modules/react-native-voxeet-conferencekit/ios/Carthage/Build/iOS/VoxeetConferenceKit.framework

6. Go to your target settings -> 'Build Settings' in `All` (the default view is in `Basic` mode):
- **FRAMEWORK_SEARCH_PATHS** = $(PROJECT_DIR)/../node_modules/react-native-voxeet-conferencekit/ios/Carthage/Build/iOS
- **ALWAYS_EMBED_SWIFT_STANDARD_LIBRARIES** = YES

7. If you are using **ExpoKit** there is an extra step (https://docs.expo.io/versions/latest/expokit/expokit#ios):
Select RNVoxeetConferencekit.xcodeproj and go to the target settings -> 'Build Settings':
- **HEADER_SEARCH_PATHS** = $(PROJECT_DIR)/../../../ios/Pods/Headers/Public (in `recursive`)

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativeVoxeetConferencekitPackage;` to the imports at the top of the file
  - Add `new RNReactNativeVoxeetConferencekitPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-voxeet-conferencekit'
  	project(':react-native-voxeet-conferencekit').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-voxeet-conferencekit/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-voxeet-conferencekit')
  	```
### Mandatory modification

**Warning : those modification are not done automatically by `react-native link`. You must set them !**

#### Android

You must edit those files :
- `MainActivity`
- `MainApplication`
- `app/build.gradle`
- `app/src/main/AndroidManifest.xml`

##### app/build.gradle

The SDK currently uses the recyclerview:27.1.1

Modify your app/build.gradle to add
```
dependencies {
  implementation 'com.android.support:recyclerview-v7:27.1.1'
}
```

##### MainApplication

`new RNVoxeetConferencekitPackage()` becomes `new RNVoxeetConferencekitPackage(this)`

##### MainActivity

- if your `MainActivity` extends `ReactActivity`, change `MainActivity extends ReactActivity` to `MainActivity extends RNVoxeetActivity`

with the following import :
`import com.voxeet.specifics.RNVoxeetActivity`

- if you are using `Expo` or you do the above modification
Using Android Studio, copy paste the following method calls in the `MainActivity.java` file :
* In the imports :
```
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
```

* In the code :
```
  private RNVoxeetActivityObject mActivityObject;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      mActivityObject = new RNVoxeetActivityObject();
      mActivityObject.onCreate(this);
  }

  @Override
  protected void onResume() {
      super.onResume();

      mActivityObject.onResume(this);
  }

  @Override
  protected void onPause() {
      mActivityObject.onPause(this);

      super.onPause();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      mActivityObject.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void onNewIntent(Intent intent) {
      super.onNewIntent(intent);

      mActivityObject.onNewIntent(intent);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!mActivityObject.onActivityResult(requestCode, resultCode, data)) {
          super.onActivityResult(requestCode, resultCode, data);
      }
  }
```

#### app/build.gradle

VoxeetSDK uses Java 8 instructions. Please edit the app/build.gradle to incorporate this compilation mode :

```
android {
    ...

    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }

    ...
}
```

#### app/src/main/AndroidManifest.xml

After the `permissions` required, add those xml nodes :

```
  <!-- VOXEET PERMISSION - WARNING : THERE MAY BE DUPLICATES - no expected issues -->

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
import RNReactNativeVoxeetConferencekit from 'react-native-voxeet-conferencekit';

RNReactNativeVoxeetConferencekit;
```

## Configuration

Depending on your Environment, you must configurate your project according to the public documentation

### iOS

Please go to [iOS Conferencekit](https://github.com/voxeet/voxeet-ios-conferencekit)

### Android

Please go to [Android SDK Sample](https://github.com/voxeet/android-sdk-sample)
