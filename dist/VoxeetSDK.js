import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from 'react-native';
var RNVoxeetConferencekit = NativeModules.RNVoxeetConferencekit;
;
;
var _VoxeetSDK = /** @class */ (function () {
    function _VoxeetSDK() {
        this.refreshAccessTokenCallback = null;
    }
    _VoxeetSDK.prototype.initialize = function (consumerKey, consumerSecret) {
        return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret);
    };
    _VoxeetSDK.prototype.initializeToken = function (accessToken, refreshToken) {
        var _this = this;
        if (!this.refreshAccessTokenCallback) {
            this.refreshAccessTokenCallback = function () {
                refreshToken()
                    .then(function (token) { return RNVoxeetConferencekit.onAccessTokenOk(token); })
                    .catch(function (err) {
                    RNVoxeetConferencekit.onAccessTokenKo("Token retrieval error");
                });
            };
            var eventEmitter = Platform.OS == "android" ? DeviceEventEmitter : new NativeEventEmitter(RNVoxeetConferencekit);
            eventEmitter.addListener("refreshToken", function (e) {
                _this.refreshAccessTokenCallback && _this.refreshAccessTokenCallback();
            });
        }
        return RNVoxeetConferencekit.initializeToken(accessToken);
    };
    _VoxeetSDK.prototype.connect = function (userInfo) {
        return RNVoxeetConferencekit.connect(userInfo);
    };
    _VoxeetSDK.prototype.disconnect = function () {
        return RNVoxeetConferencekit.disconnect();
    };
    _VoxeetSDK.prototype.create = function (options) {
        return RNVoxeetConferencekit.create(options);
    };
    _VoxeetSDK.prototype.join = function (conferenceId, options) {
        if (options === void 0) { options = {}; }
        return RNVoxeetConferencekit.join(conferenceId, options);
    };
    _VoxeetSDK.prototype.leave = function () {
        return RNVoxeetConferencekit.leave();
    };
    _VoxeetSDK.prototype.invite = function (conferenceId, participants) {
        return RNVoxeetConferencekit.invite(conferenceId, participants);
    };
    _VoxeetSDK.prototype.sendBroadcastMessage = function (message) {
        return RNVoxeetConferencekit.sendBroadcastMessage(message);
    };
    _VoxeetSDK.prototype.isTelecomMode = function () {
        return RNVoxeetConferencekit.isTelecomMode();
    };
    _VoxeetSDK.prototype.isAudio3DEnabled = function () {
        return RNVoxeetConferencekit.isAudio3DEnabled();
    };
    _VoxeetSDK.prototype.appearMaximized = function (enable) {
        RNVoxeetConferencekit.appearMaximized(enable);
        return true;
    };
    _VoxeetSDK.prototype.defaultBuiltInSpeaker = function (enable) {
        RNVoxeetConferencekit.defaultBuiltInSpeaker(enable);
        return true;
    };
    _VoxeetSDK.prototype.defaultVideo = function (enable) {
        RNVoxeetConferencekit.defaultVideo(enable);
        return true;
    };
    /*
      *  Android methods
      */
    _VoxeetSDK.prototype.screenAutoLock = function (activate) {
        if (Platform.OS == "android") {
            RNVoxeetConferencekit.screenAutoLock(activate);
        }
    };
    //deprecated
    _VoxeetSDK.prototype.isUserLoggedIn = function () {
        return RNVoxeetConferencekit.isUserLoggedIn();
    };
    _VoxeetSDK.prototype.checkForAwaitingConference = function () {
        if (Platform.OS != "android")
            return new Promise(function (r) { return r(); });
        return RNVoxeetConferencekit.checkForAwaitingConference();
    };
    /*
      *  Deprecated methods
      */
    _VoxeetSDK.prototype.startConference = function (conferenceId, participants) {
        return RNVoxeetConferencekit.startConference(conferenceId, participants);
    };
    _VoxeetSDK.prototype.stopConference = function () {
        return RNVoxeetConferencekit.leave();
    };
    _VoxeetSDK.prototype.openSession = function (userInfo) {
        return RNVoxeetConferencekit.connect(userInfo);
    };
    _VoxeetSDK.prototype.closeSession = function () {
        return RNVoxeetConferencekit.disconnect();
    };
    return _VoxeetSDK;
}());
export default _VoxeetSDK;
//# sourceMappingURL=VoxeetSDK.js.map