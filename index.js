import { NativeModules, Platform } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;

import VoxeetEvents from "./lib/VoxeetEvents";
import VideoView from "./lib/VideoView";
import VoxeetSDK from "./lib/VoxeetSDK";

import {
    ConferenceUser,
    MediaStream
} from "./lib/VoxeetTypes";

const Types = {
    ConferenceUser,
    MediaStream
}
export {
    VoxeetSDK,
    VideoView,
    Types
};
