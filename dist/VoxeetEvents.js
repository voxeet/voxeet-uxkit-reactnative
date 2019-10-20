import { NativeModules, NativeEventEmitter } from 'react-native';
var RNVoxeetConferencekit = NativeModules.RNVoxeetConferencekit;
var VoxeetEvents = /** @class */ (function () {
    function VoxeetEvents() {
        this.events = new NativeEventEmitter(RNVoxeetConferencekit);
    }
    return VoxeetEvents;
}());
export default VoxeetEvents;
//# sourceMappingURL=VoxeetEvents.js.map