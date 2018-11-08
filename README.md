
# react-native-voxeet-conferencekit

## Getting started

`$ npm install react-native-voxeet-conferencekit --save`

### Mostly automatic installation

`$ react-native link react-native-voxeet-conferencekit`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-voxeet-conferencekit` and add `RNReactNativeVoxeetConferencekit.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNReactNativeVoxeetConferencekit.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

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
