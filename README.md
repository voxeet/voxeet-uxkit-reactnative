# Voxeet UXKit React Native


## SDK 3.0 License agreement

Before using the latest version of the react-native plugin, please review and accept the [Dolby Software License Agreement](https://github.com/voxeet/voxeet-sdk-android/blob/main/LICENSE).

## Documentation

A full documentation can be found here: https://dolby.io/developers/interactivity-apis/client-ux-kit/uxkit-voxeet-react-native.

## Getting started

`$ npm install @voxeet/react-native-voxeet-conferencekit --save`

### Mostly automatic installation

`$ react-native link @voxeet/react-native-voxeet-conferencekit`

**_Note: for iOS & Android, you need to do some [mandatory modification](https://github.com/voxeet/voxeet-uxkit-reactnative#mandatory-modification)_ to your project**

### Manual installation

#### Android

**Note: to enable Firebase on Android (for Push Notification), please add also the [react-native-voxeet-firebase](https://github.com/voxeet/react-native-voxeet-firebase) library**

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativeVoxeetConferencekitPackage;` to the imports at the top of the file
  - Add `voxeet` to the list returned by the `getPackages()` method
  - Add a field in the `MainApplication` class named voxeet : `private RNReactNativeVoxeetConferencekitPackage voxeet;`
  - in the `onCreate` method, instantiate the `voxeet` field to a new instance of the `RNReactNativeVoxeetConferencekitPackage` class : `voxeet = new RNReactNativeVoxeetConferencekitPackage(MainApplication.this);`

2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':@voxeet_react-native-voxeet-conferencekit'
  	project(':@voxeet_react-native-voxeet-conferencekit').projectDir = new File(rootProject.projectDir, 	'../node_modules/@voxeet/react-native-voxeet-conferencekit/android')
  	```

3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile (project(':@voxeet_react-native-voxeet-conferencekit')) {
          transitive = true
      }
  	```

## Mandatory modification

### iOS (react-native >= 0.60)

1. Open your Xcode project from YOUR_PROJECT/ios/YOUR_PROJECT.xcworkspace

2. Go to your target settings -> 'Signing & Capabilities' -> '+ Capability' -> 'Background Modes'
- Turn on 'Audio, AirPlay and Picture in Picture'  
- Turn on 'Voice over IP'

If you want to support CallKit (receiving incoming call when application is killed) with VoIP push notification, enable 'Push Notifications' (you will need to upload your [VoIP push certificate](https://developer.apple.com/account/ios/certificate/) to the Voxeet developer portal).

3. Privacy **permissions**, add two new keys in the Info.plist:
- Privacy - Microphone Usage Description
- Privacy - Camera Usage Description

4. Open a terminal and go to YOUR_PROJECT/ios

```bash
pod install
```

##### Xcode 12 - additional step:

Open your .xcworkspace project, select Product > Scheme > Edit scheme > Build > Uncheck "Parallelize Build"

##### Check your podfile:

For v3.0.0, bitcode is currently unsupported. In your podfile, check that this block of code is present :
```
platform :ios, '11.0'
use_frameworks!

...

  post_install do |installer|
    installer.pods_project.targets.each do |target|
      if target.name == "VoxeetSDK" || target.name == "VoxeetUXKit" || target.name == "voxeet-uxkit-reactnative"
        target.build_configurations.each do |config|
          config.build_settings["ENABLE_BITCODE"] = "NO"
        end
      end
    end
  end
```

### Android

**Warning : those modification are not done automatically by `react-native link`. You must set them !**

You must edit those files :
- `app/src/main/AndroidManifest.xml`
- `MainActivity`
- `MainApplication`

##### MainApplication

Follow the implementation from the `link` documentation, first step


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
import { VoxeetSDK, ConferenceUser } from "@voxeet/react-native-voxeet-conferencekit";
...
```

## Configuration

Depending on your Environment, you must configurate your project according to the public documentation

### iOS

Please go to [iOS VoxeetUXKit](https://github.com/voxeet/voxeet-uxkit-ios)

### Android

Please go to [Android SDK Sample](https://github.com/voxeet/android-sdk-sample)

## Build locally

To build locally 

```bash
npm run build-library
```

the typescript command line needs local dev resolutions (available in the `package.json`)

```bash
npm i -D @types/react ...
```

## License

```
   Copyright 2020 - Voxeet

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

© Voxeet, 2020