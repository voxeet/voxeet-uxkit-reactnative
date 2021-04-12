import ConferenceUser from './types/ConferenceUser';
import Participant from './types/Participant';
import MediaStream from './types/MediaStream';
import { CreateOptions, CreateResult } from './types/CreateConference';
import { JoinOptions, JoinResult } from './types/JoinConference';
export interface RefreshCallback {
    (): void;
}
export interface TokenRefreshCallback {
    (): Promise<string>;
}
declare class RNVoxeetSDK {
    #private;
    get events(): any;
    set events(any: any);
    refreshAccessTokenCallback: RefreshCallback | null;
    /**
     * Initializes the SDK using the customer key and secret.
     * @param consumerKey Consumer Key
     * @param consumerSecret Consumer Secret
     * @param deactivateOverlay Optional value to deactivate the whole overlay if the react native will take care of displaying specific UI
     */
    initialize(consumerKey: string, consumerSecret: string, deactivateOverlay?: boolean): Promise<boolean>;
    /**
     * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
     * @param accessToken Access token
     * @param refreshToken Callback to get a new access token after it expires
     * @param deactivateOverlay Optional value to deactivate the whole overlay if the react native will take care of displaying specific UI
     */
    initializeToken(accessToken: string | undefined, refreshToken: TokenRefreshCallback, deactivateOverlay?: boolean): Promise<boolean>;
    /**
     * Opens a new session.
     * @param userInfo Participant information
     */
    connect(userInfo: ConferenceUser): Promise<boolean>;
    /**
     * Closes the current session.
     */
    disconnect(): Promise<boolean>;
    /**
     * Creates a conference.
     * @param options Options to use to create the conference
     */
    create(options: CreateOptions): Promise<CreateResult>;
    /**
     * Joins the conference.
     * @param conferenceId Id of the conference to join
     * @param options Options to use to join the conference
     */
    join(conferenceId: string, options?: JoinOptions): Promise<JoinResult>;
    /**
     * Leaves the conference.
     */
    leave(): Promise<boolean>;
    /**
     * Starts the local video
     */
    startVideo(): Promise<boolean>;
    /**
     * Stops the local video
     */
    stopVideo(): Promise<boolean>;
    /**
     * Invite a participant to the conference.
     * @param conferenceId Id of the conference to invite the participant to
     * @param participants List of participants to invite
     */
    invite(conferenceId: string, participants: ConferenceUser[]): Promise<boolean>;
    /**
     * Get the list of participants
     * @param conferenceId Id of the conference to get the participants from
     * @returns List of participants in the conference
     */
    participants(conferenceId: string): Promise<Participant[]>;
    /**
     * Get the list of streams for a given participant
     * @param participantId Id of the participant to get the streams from
     * @returns List of streams for this participant
     */
    streams(participantId: string): Promise<MediaStream[]>;
    /**
     * Sends a broadcast message to the participants of the conference.
     * @param message Message to send to the other participants
     */
    sendBroadcastMessage(message: string): Promise<boolean>;
    /**
     * Is telecom mode enabled.
     */
    isTelecomMode(): Promise<boolean>;
    /**
     * Is audio 3D enabled.
     */
    isAudio3DEnabled(): Promise<boolean>;
    /**
     * Sets if you want the UXKit to appear maximized or not.
     * @param maximized True to have the UXKit to appear maximized
     */
    appearMaximized(maximized: boolean): boolean;
    /**
     * Use the built in speaker by default.
     * @param enable True to use the built in speaker by default
     */
    defaultBuiltInSpeaker(enable: boolean): boolean;
    /**
     * Sets the video on by default.
     * @param enable True to turn on the video by default
     */
    defaultVideo(enable: boolean): boolean;
    /**
     * Activates or disable the screen auto lock. Android only.
     * @param activate True to activate the screen auto lock
     */
    screenAutoLock(activate: boolean): void;
    /** @deprecated */
    isUserLoggedIn(): Promise<boolean>;
    /**
     * Checks if a conference is awaiting. Android only.
     */
    checkForAwaitingConference(): Promise<boolean>;
    /** @deprecated Use join() instead. */
    startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise<boolean>;
    /** @deprecated Use leave() instead. */
    stopConference(): Promise<boolean>;
    /** @deprecated Use connect() instead. */
    openSession(userInfo: ConferenceUser): Promise<boolean>;
    /** @deprecated Use disconnect() instead. */
    closeSession(): Promise<boolean>;
    static VoxeetSDK: RNVoxeetSDK;
}
declare const _default: RNVoxeetSDK;
export default _default;
