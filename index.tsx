import VoxeetEvents from "./lib/VoxeetEvents";
import VideoView from "./lib/VideoView";
import { default as _VoxeetSDK } from "./lib/VoxeetSDK";

import {
    ConferenceUser,
    MediaStream
} from "./lib/VoxeetTypes";

const Types = {
    ConferenceUser,
    MediaStream
}

const VoxeetSDK = new _VoxeetSDK();

export {
    VoxeetSDK,
    VideoView,
    Types
};
