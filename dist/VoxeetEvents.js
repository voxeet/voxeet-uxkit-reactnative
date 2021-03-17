import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;
export default class VoxeetEvents {
    constructor() {
        this.events = new NativeEventEmitter(RNVoxeetConferencekit);
    }
    addListener(type, listener) {
        this.events.addListener(type, listener);
    }
    removeListener(type, listener) {
        this.events.removeListener(type, listener);
    }
}
//# sourceMappingURL=VoxeetEvents.js.map