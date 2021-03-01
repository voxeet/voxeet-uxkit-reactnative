import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from 'react-native';
var RNVoxeetConferencekit = NativeModules.RNVoxeetConferencekit;
;
;
var _VoxeetSDK = /** @class */ (function () {
    function _VoxeetSDK() {
        this.refreshAccessTokenCallback = null;
    }
    /**
     * Initializes the SDK using the customer key and secret.
     * @param consumerKey Consumer Key
     * @param consumerSecret Consumer Secret
     */
    _VoxeetSDK.prototype.initialize = function (consumerKey, consumerSecret) {
        return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret);
    };
    /**
     * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
     * @param accessToken Access token
     * @param refreshToken Callback to get a new access token after it expires
     */
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
    /**
     * Opens a new session.
     * @param userInfo Participant information
     */
    _VoxeetSDK.prototype.connect = function (userInfo) {
        return RNVoxeetConferencekit.connect(userInfo);
    };
    /**
     * Closes the current session.
     */
    _VoxeetSDK.prototype.disconnect = function () {
        return RNVoxeetConferencekit.disconnect();
    };
    /**
     * Creates a conference.
     * @param options Options to use to create the conference
     */
    _VoxeetSDK.prototype.create = function (options) {
        return RNVoxeetConferencekit.create(options);
    };
    /**
     * Joins the conference.
     * @param conferenceId Id of the conference to join
     * @param options Options to use to join the conference
     */
    _VoxeetSDK.prototype.join = function (conferenceId, options) {
        if (options === void 0) { options = {}; }
        return RNVoxeetConferencekit.join(conferenceId, options);
    };
    /**
     * Leaves the conference.
     */
    _VoxeetSDK.prototype.leave = function () {
        return RNVoxeetConferencekit.leave();
    };
    /**
     * Invite a participant to the conference.
     * @param conferenceId Id of the conference to invite the participant to
     * @param participants List of participants to invite
     */
    _VoxeetSDK.prototype.invite = function (conferenceId, participants) {
        return RNVoxeetConferencekit.invite(conferenceId, participants);
    };
    /**
     * Sends a broadcast message to the participants of the conference.
     * @param message Message to send to the other participants
     */
    _VoxeetSDK.prototype.sendBroadcastMessage = function (message) {
        return RNVoxeetConferencekit.sendBroadcastMessage(message);
    };
    /**
     * Is telecom mode enabled.
     */
    _VoxeetSDK.prototype.isTelecomMode = function () {
        return RNVoxeetConferencekit.isTelecomMode();
    };
    /**
     * Is audio 3D enabled.
     */
    _VoxeetSDK.prototype.isAudio3DEnabled = function () {
        return RNVoxeetConferencekit.isAudio3DEnabled();
    };
    /**
     * Sets if you want the UXKit to appear maximized or not.
     * @param maximized True to have the UXKit to appear maximized
     */
    _VoxeetSDK.prototype.appearMaximized = function (maximized) {
        RNVoxeetConferencekit.appearMaximized(maximized);
        return true;
    };
    /**
     * Use the built in speaker by default.
     * @param enable True to use the built in speaker by default
     */
    _VoxeetSDK.prototype.defaultBuiltInSpeaker = function (enable) {
        RNVoxeetConferencekit.defaultBuiltInSpeaker(enable);
        return true;
    };
    /**
     * Sets the video on by default.
     * @param enable True to turn on the video by default
     */
    _VoxeetSDK.prototype.defaultVideo = function (enable) {
        RNVoxeetConferencekit.defaultVideo(enable);
        return true;
    };
    /**
     * Activates or disable the screen auto lock. Android only.
     * @param activate True to activate the screen auto lock
     */
    _VoxeetSDK.prototype.screenAutoLock = function (activate) {
        if (Platform.OS == "android") {
            RNVoxeetConferencekit.screenAutoLock(activate);
        }
    };
    /** @deprecated */
    _VoxeetSDK.prototype.isUserLoggedIn = function () {
        return RNVoxeetConferencekit.isUserLoggedIn();
    };
    /**
     * Checks if a conference is awaiting. Android only.
     */
    _VoxeetSDK.prototype.checkForAwaitingConference = function () {
        if (Platform.OS != "android")
            return new Promise(function (r) { return r(false); });
        return RNVoxeetConferencekit.checkForAwaitingConference();
    };
    /** @deprecated Use join() instead. */
    _VoxeetSDK.prototype.startConference = function (conferenceId, participants) {
        return RNVoxeetConferencekit.startConference(conferenceId, participants);
    };
    /** @deprecated Use leave() instead. */
    _VoxeetSDK.prototype.stopConference = function () {
        return this.leave();
    };
    /** @deprecated Use connect() instead. */
    _VoxeetSDK.prototype.openSession = function (userInfo) {
        return this.connect(userInfo);
    };
    /** @deprecated Use disconnect() instead. */
    _VoxeetSDK.prototype.closeSession = function () {
        return this.disconnect();
    };
    return _VoxeetSDK;
}());
export default _VoxeetSDK;
//# sourceMappingURL=VoxeetSDK.js.map