import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;


import type {
  ConferenceUser,
  MediaStream
} from "./VoxeetTypes";

export default class VoxeetEvents {
  events = new NativeEventEmitter(RNVoxeetConferencekit);

  constructor() {

  }
}
