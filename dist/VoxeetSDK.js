var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var _RNVoxeetSDK_events;
import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from 'react-native';
import VoxeetEvents from './VoxeetEvents';
import Participant from './types/Participant';
const { RNVoxeetConferencekit } = NativeModules;
;
;
class RNVoxeetSDK {
    constructor() {
        _RNVoxeetSDK_events.set(this, new VoxeetEvents());
        this.refreshAccessTokenCallback = null;
    }
    get events() { return __classPrivateFieldGet(this, _RNVoxeetSDK_events, "f"); }
    set events(any) { }
    /**
     * Initializes the SDK using the customer key and secret.
     * @param consumerKey Consumer Key
     * @param consumerSecret Consumer Secret
     * @param deactivateOverlay Optional value to deactivate the whole overlay if the react native will take care of displaying specific UI
     */
    initialize(consumerKey, consumerSecret, deactivateOverlay) {
        return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret, !!deactivateOverlay);
    }
    /**
     * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
     * @param accessToken Access token
     * @param refreshToken Callback to get a new access token after it expires
     * @param deactivateOverlay Optional value to deactivate the whole overlay if the react native will take care of displaying specific UI
     */
    initializeToken(accessToken, refreshToken, deactivateOverlay) {
        if (!this.refreshAccessTokenCallback) {
            this.refreshAccessTokenCallback = () => {
                refreshToken()
                    .then(token => RNVoxeetConferencekit.onAccessTokenOk(token))
                    .catch(err => {
                    RNVoxeetConferencekit.onAccessTokenKo("Token retrieval error");
                });
            };
            const eventEmitter = Platform.OS == "android" ? DeviceEventEmitter : new NativeEventEmitter(RNVoxeetConferencekit);
            eventEmitter.addListener("refreshToken", (e) => {
                this.refreshAccessTokenCallback && this.refreshAccessTokenCallback();
            });
        }
        return RNVoxeetConferencekit.initializeToken(accessToken, !!deactivateOverlay);
    }
    /**
     * Opens a new session.
     * @param userInfo Participant information
     */
    connect(userInfo) {
        return RNVoxeetConferencekit.connect(userInfo);
    }
    /**
     * Closes the current session.
     */
    disconnect() {
        return RNVoxeetConferencekit.disconnect();
    }
    /**
     * Creates a conference.
     * @param options Options to use to create the conference
     */
    create(options) {
        return RNVoxeetConferencekit.create(options);
    }
    /**
     * Joins the conference.
     * @param conferenceId Id of the conference to join
     * @param options Options to use to join the conference
     */
    join(conferenceId, options = {}) {
        return RNVoxeetConferencekit.join(conferenceId, options);
    }
    /**
     * Gets the current conference or undefined if none is live.
     */
    current() {
        return RNVoxeetConferencekit.current();
    }
    /**
     * Leaves the conference.
     */
    leave() {
        return RNVoxeetConferencekit.leave();
    }
    /**
     * Starts the local video
     */
    startVideo() {
        return RNVoxeetConferencekit.startVideo();
    }
    /**
     * Stops the local video
     */
    stopVideo() {
        return RNVoxeetConferencekit.stopVideo();
    }
    /**
     * Invite a participant to the conference.
     * @param conferenceId Id of the conference to invite the participant to
     * @param participants List of participants to invite
     */
    invite(conferenceId, participants) {
        return RNVoxeetConferencekit.invite(conferenceId, participants);
    }
    /**
     * Get the list of participants
     * @param conferenceId Id of the conference to get the participants from
     * @returns List of participants in the conference
     */
    participants(conferenceId) {
        return RNVoxeetConferencekit.participants(conferenceId)
            .then((result) => result.map(r => new Participant(r.participantId || "", r.status, r.externalId, r.name, r.avatarUrl)));
    }
    /**
     * Get the list of streams for a given participant
     * @param participantId Id of the participant to get the streams from
     * @returns List of streams for this participant
     */
    streams(participantId) {
        return RNVoxeetConferencekit.streams(participantId);
    }
    /**
     * Sends a broadcast message to the participants of the conference.
     * @param message Message to send to the other participants
     */
    sendBroadcastMessage(message) {
        return RNVoxeetConferencekit.sendBroadcastMessage(message);
    }
    /**
     * Is telecom mode enabled.
     */
    isTelecomMode() {
        return RNVoxeetConferencekit.isTelecomMode();
    }
    /**
     * Is audio 3D enabled.
     */
    isAudio3DEnabled() {
        return RNVoxeetConferencekit.isAudio3DEnabled();
    }
    /**
     * Sets if you want the UXKit to appear maximized or not.
     * @param maximized True to have the UXKit to appear maximized
     */
    appearMaximized(maximized) {
        RNVoxeetConferencekit.appearMaximized(maximized);
        return true;
    }
    /**
     * Use the built in speaker by default.
     * @param enable True to use the built in speaker by default
     */
    defaultBuiltInSpeaker(enable) {
        RNVoxeetConferencekit.defaultBuiltInSpeaker(enable);
        return true;
    }
    /**
     * Sets the video on by default.
     * @param enable True to turn on the video by default
     */
    defaultVideo(enable) {
        RNVoxeetConferencekit.defaultVideo(enable);
        return true;
    }
    /**
     * Activates or disable the screen auto lock. Android only.
     * @param activate True to activate the screen auto lock
     */
    screenAutoLock(activate) {
        if (Platform.OS == "android") {
            RNVoxeetConferencekit.screenAutoLock(activate);
        }
    }
    /** @deprecated */
    isUserLoggedIn() {
        return RNVoxeetConferencekit.isUserLoggedIn();
    }
    /**
     * Checks if a conference is awaiting. Android only.
     * @deprecated
     */
    checkForAwaitingConference() {
        return Promise.resolve(false);
    }
    /** @deprecated Use join() instead. */
    startConference(conferenceId, participants) {
        return RNVoxeetConferencekit.startConference(conferenceId, participants);
    }
    /** @deprecated Use leave() instead. */
    stopConference() {
        return this.leave();
    }
    /** @deprecated Use connect() instead. */
    openSession(userInfo) {
        return this.connect(userInfo);
    }
    /** @deprecated Use disconnect() instead. */
    closeSession() {
        return this.disconnect();
    }
}
_RNVoxeetSDK_events = new WeakMap();
RNVoxeetSDK.VoxeetSDK = new RNVoxeetSDK();
export default new RNVoxeetSDK();
//# sourceMappingURL=VoxeetSDK.js.map