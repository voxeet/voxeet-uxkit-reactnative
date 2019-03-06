import VoxeetEvents from "./lib/VoxeetEvents";
import VideoView from "./lib/VideoView";
import { default as _VoxeetSDK } from "./lib/VoxeetSDK";


const VoxeetSDK = new _VoxeetSDK();

export {
  ConferenceUser,
  CreateConference,
  JoinConference,
  MediaStream
} from "./lib/types";


export {
    VoxeetSDK,
    VideoView
};
