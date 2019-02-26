import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;

export default class VoxeetEvents {
  events = new NativeEventEmitter(RNVoxeetConferencekit);

  constructor() {

  }
}
