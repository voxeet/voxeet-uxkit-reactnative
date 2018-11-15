import { NativeModules, Platform } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;

import type {
  ConferenceUser,
  MediaStream
} from "./VoxeetTypes";

class VoxeetSDK {

  debug(): string {
    return RNVoxeetConferencekit.debug();
  }

  initialize(consumerKey: string, consumerSecret: string): Promise {
    return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret);
  }

  connect(userInfo: ConferenceUser): Promise {
    return RNVoxeetConferencekit.openSession(userInfo);
  }

  disconnect(): Promise {
    return RNVoxeetConferencekit.disconnect();
  }

  create(parameters: any): Promise {
    return RNVoxeetConferencekit.create(parameters);
  }

  join(conferenceId: string): Promise {
    return RNVoxeetConferencekit.join(conferenceId);
  }

  leave(): Promise {
    return RNVoxeetConferencekit.leave();
  }

  invite(conferenceId: string, participants: ConferenceUser[]): Promise {
    return RNVoxeetConferencekit.invite(conferenceId, participants);
  }

  sendBroadcastMessage(message: string): Promise {
    return RNVoxeetConferencekit.sendBroadcastMessage(message);
  }

  appearMaximized(activate: boolean) {
    RNVoxeetConferencekit.appearMaximized(activate);
  }

  defaultBuiltInSpeaker(activate: boolean) {
    RNVoxeetConferencekit.defaultBuiltInSpeaker(activate);
  }

  defaultVideo(enabled: boolean) {
    RNVoxeetConferencekit.defaultVideo(enabled);
  }

  /*
   *  Android methods
   */

  screenAutoLock(activate: boolean) {
    if(Platform.os == "android") {
      RNVoxeetConferencekit.screenAutoLock(activate);
    }
  }

  //deprecated
  isUserLoggedIn(): boolean {
    return RNVoxeetConferencekit.isUserLoggedIn();
  }

  checkForAwaitingConference(): Promise {
    if(Platform.os != "android") return new Promise(r => r());

    return RNVoxeetConferencekit.checkForAwaitingConference();
  }

  /*
   *  Deprecated methods
   */
  startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise {
    return RNVoxeetConferencekit.startConference(conferenceId, participants);
  }

  stopConference(): Promise {
    return RNVoxeetConferencekit.leave();
  }

  openSession(userInfo: ConferenceUser): Promise {
    return RNVoxeetConferencekit.openSession(userInfo);
  }

  closeSession(): Promise {
    return RNVoxeetConferencekit.closeSession();
  }

}

const voxeetSDK = new VoxeetSDK();

export { VoxeetSDK as voxeetSDK };
