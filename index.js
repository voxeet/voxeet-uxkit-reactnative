import { NativeModules, Platform } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;

import VoxeetEvents from "./lib/VoxeetEvents";
import VideoView from "./lib/VideoView";
import VoxeetSDK from "./lib/VoxeetSDK";

import type {
  ConferenceUser,
  MediaStream
} from "./lib/VoxeetTypes";

export {
  VoxeetSDK,
  VideoView
};

export type {
  ConferenceUser,
  MediaStream
};
