import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;

import {
  ConferenceUser,
  MediaStream
} from "./VoxeetTypes";

interface RefreshCallback {
  (): void;
};

export interface TokenRefreshCallback {
  (): Promise<string>
};

export default class _VoxeetSDK {
  refreshAccessTokenCallback: RefreshCallback|null = null;


  initialize(consumerKey: string, consumerSecret: string): Promise<any> {
      return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret);
  }

  initializeToken(accessToken: string|undefined, refreshToken: TokenRefreshCallback) {
    if(!this.refreshAccessTokenCallback) {
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

    return RNVoxeetConferencekit.initializeToken(accessToken);
  }

  connect(userInfo: ConferenceUser): Promise<any> {
    return RNVoxeetConferencekit.connect(userInfo);
  }

  disconnect(): Promise<any> {
    return RNVoxeetConferencekit.disconnect();
  }

  create(parameters: any): Promise<any> {
    return RNVoxeetConferencekit.create(parameters);
  }

  join(conferenceId: string): Promise<any> {
    return RNVoxeetConferencekit.join(conferenceId);
  }

  leave(): Promise<any> {
    return RNVoxeetConferencekit.leave();
  }

  invite(conferenceId: string, participants: ConferenceUser[]): Promise<any> {
    return RNVoxeetConferencekit.invite(conferenceId, participants);
  }

  sendBroadcastMessage(message: string): Promise<any> {
    return RNVoxeetConferencekit.sendBroadcastMessage(message);
  }

  appearMaximized(enable: boolean): boolean {
    RNVoxeetConferencekit.appearMaximized(enable);
    return true;
  }

  defaultBuiltInSpeaker(enable: boolean): boolean {
    RNVoxeetConferencekit.defaultBuiltInSpeaker(enable);
    return true;
  }

  defaultVideo(enable: boolean): boolean {
    RNVoxeetConferencekit.defaultVideo(enable);
    return true;
  }

  /*
    *  Android methods
    */
  screenAutoLock(activate: boolean) {
    if(Platform.OS == "android") {
      RNVoxeetConferencekit.screenAutoLock(activate);
    }
  }

  //deprecated
  isUserLoggedIn(): Promise<boolean> {
    return RNVoxeetConferencekit.isUserLoggedIn();
  }

  checkForAwaitingConference(): Promise<any> {
    if(Platform.OS != "android") return new Promise(r => r());

    return RNVoxeetConferencekit.checkForAwaitingConference();
  }

  /*
    *  Deprecated methods
    */

  startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise<any> {
    return RNVoxeetConferencekit.startConference(conferenceId, participants);
  }

  stopConference(): Promise<any> {
    return RNVoxeetConferencekit.leave();
  }

  openSession(userInfo: ConferenceUser): Promise<any> {
    return RNVoxeetConferencekit.connect(userInfo);
  }

  closeSession(): Promise<any> {
    return RNVoxeetConferencekit.disconnect();
  }
}