import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from 'react-native';
import VoxeetEvents from './VoxeetEvents';
import ConferenceUser from './types/ConferenceUser';
import Participant from './types/Participant';
import MediaStream, { MediaStreamType } from './types/MediaStream';
import { CreateOptions, CreateResult } from './types/CreateConference';
import { JoinOptions, JoinResult } from './types/JoinConference';

const { RNVoxeetConferencekit } = NativeModules;

export interface RefreshCallback {
  (): void;
};

export interface TokenRefreshCallback {
  (): Promise<string>
};

class RNVoxeetSDK {

  #events = new VoxeetEvents();
  get events() { return this.#events; }
  set events(any: any) { }

  refreshAccessTokenCallback: RefreshCallback | null = null;

  /**
   * Initializes the SDK using the customer key and secret.
   * @param consumerKey Consumer Key
   * @param consumerSecret Consumer Secret
   * @param deactivateOverlay Optional value to deactivate the whole overlay if the react native will take care of displaying specific UI
   */
  initialize(consumerKey: string, consumerSecret: string, deactivateOverlay?: boolean): Promise<boolean> {
    return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret, !!deactivateOverlay);
  }

  /**
   * Initializes the SDK with an access token that is provided by the customer backend communicating with Voxeet servers.
   * @param accessToken Access token
   * @param refreshToken Callback to get a new access token after it expires
   * @param deactivateOverlay Optional value to deactivate the whole overlay if the react native will take care of displaying specific UI
   */
  initializeToken(accessToken: string | undefined, refreshToken: TokenRefreshCallback, deactivateOverlay?: boolean): Promise<boolean> {
    if (!this.refreshAccessTokenCallback) {
      this.refreshAccessTokenCallback = () => {
        refreshToken()
          .then(token => RNVoxeetConferencekit.onAccessTokenOk(token))
          .catch(err => {
            RNVoxeetConferencekit.onAccessTokenKo("Token retrieval error");
          });
      }
      const eventEmitter = Platform.OS == "android" ? DeviceEventEmitter : new NativeEventEmitter(RNVoxeetConferencekit);
      eventEmitter.addListener("refreshToken", (e: Event) => {
        this.refreshAccessTokenCallback && this.refreshAccessTokenCallback();
      });
    }

    return RNVoxeetConferencekit.initializeToken(accessToken, !!deactivateOverlay);
  }

  /**
   * Opens a new session.
   * @param userInfo Participant information
   */
  connect(userInfo: ConferenceUser): Promise<boolean> {
    return RNVoxeetConferencekit.connect(userInfo);
  }

  /**
   * Closes the current session.
   */
  disconnect(): Promise<boolean> {
    return RNVoxeetConferencekit.disconnect();
  }

  /**
   * Creates a conference.
   * @param options Options to use to create the conference
   */
  create(options: CreateOptions): Promise<CreateResult> {
    return RNVoxeetConferencekit.create(options);
  }

  /**
   * Joins the conference.
   * @param conferenceId Id of the conference to join
   * @param options Options to use to join the conference
   */
  join(conferenceId: string, options: JoinOptions = {}): Promise<JoinResult> {
    return RNVoxeetConferencekit.join(conferenceId, options);
  }

  /**
   * Leaves the conference.
   */
  leave(): Promise<boolean> {
    return RNVoxeetConferencekit.leave();
  }

  /**
   * Invite a participant to the conference.
   * @param conferenceId Id of the conference to invite the participant to
   * @param participants List of participants to invite
   */
  invite(conferenceId: string, participants: ConferenceUser[]): Promise<boolean> {
    return RNVoxeetConferencekit.invite(conferenceId, participants);
  }

  /**
   * Get the list of participants
   * @param conferenceId Id of the conference to get the participants from
   * @returns List of participants in the conference
   */
  participants(conferenceId: string): Promise<Participant[]> {
    return RNVoxeetConferencekit.participants(conferenceId)
    .then((result: any[]) => result.map(r => new Participant(
      r.participantId || "", r.status, r.externalId, r.name, r.avatarUrl
    )));
  }

  /**
   * Get the list of streams for a given participant
   * @param participantId Id of the participant to get the streams from
   * @returns List of streams for this participant
   */
  streams(participantId: string): Promise<MediaStream[]> {
    return RNVoxeetConferencekit.streams(participantId)
    .then((result: any[]) => result.map(r => ({
      peerId: participantId,
      streamId: r.streamId,
      hasVideoTracks: r.hasVideoTracks,
      hasAudioTracks: r.hasAudioTracks,
      type: MediaStreamType[r.type] || MediaStreamType.Camera
    })));
  }

  /**
   * Sends a broadcast message to the participants of the conference.
   * @param message Message to send to the other participants
   */
  sendBroadcastMessage(message: string): Promise<boolean> {
    return RNVoxeetConferencekit.sendBroadcastMessage(message);
  }

  /**
   * Is telecom mode enabled.
   */
  isTelecomMode(): Promise<boolean> {
    return RNVoxeetConferencekit.isTelecomMode();
  }

  /**
   * Is audio 3D enabled.
   */
  isAudio3DEnabled(): Promise<boolean> {
    return RNVoxeetConferencekit.isAudio3DEnabled();
  }

  /**
   * Sets if you want the UXKit to appear maximized or not.
   * @param maximized True to have the UXKit to appear maximized
   */
  appearMaximized(maximized: boolean): boolean {
    RNVoxeetConferencekit.appearMaximized(maximized);
    return true;
  }

  /**
   * Use the built in speaker by default.
   * @param enable True to use the built in speaker by default
   */
  defaultBuiltInSpeaker(enable: boolean): boolean {
    RNVoxeetConferencekit.defaultBuiltInSpeaker(enable);
    return true;
  }

  /**
   * Sets the video on by default.
   * @param enable True to turn on the video by default
   */
  defaultVideo(enable: boolean): boolean {
    RNVoxeetConferencekit.defaultVideo(enable);
    return true;
  }

  /**
   * Activates or disable the screen auto lock. Android only.
   * @param activate True to activate the screen auto lock
   */
  screenAutoLock(activate: boolean) {
    if (Platform.OS == "android") {
      RNVoxeetConferencekit.screenAutoLock(activate);
    }
  }

  /** @deprecated */
  isUserLoggedIn(): Promise<boolean> {
    return RNVoxeetConferencekit.isUserLoggedIn();
  }

  /**
   * Checks if a conference is awaiting. Android only.
   */
  checkForAwaitingConference(): Promise<boolean> {
    if (Platform.OS != "android") return new Promise<boolean>(r => r(false));

    return RNVoxeetConferencekit.checkForAwaitingConference();
  }

  /** @deprecated Use join() instead. */
  startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise<boolean> {
    return RNVoxeetConferencekit.startConference(conferenceId, participants);
  }

  /** @deprecated Use leave() instead. */
  stopConference(): Promise<boolean> {
    return this.leave();
  }

  /** @deprecated Use connect() instead. */
  openSession(userInfo: ConferenceUser): Promise<boolean> {
    return this.connect(userInfo);
  }

  /** @deprecated Use disconnect() instead. */
  closeSession(): Promise<boolean> {
    return this.disconnect();
  }

  public static VoxeetSDK = new RNVoxeetSDK();
}

export default new RNVoxeetSDK();