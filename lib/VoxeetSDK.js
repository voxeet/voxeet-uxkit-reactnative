import { NativeModules, Platform } from 'react-native';
const { RNVoxeetConferencekit } = NativeModules;

import {
  ConferenceUser,
  MediaStream
} from "./VoxeetTypes";

class VoxeetSDK {

  initialize(consumerKey: string, consumerSecret: string): Promise {
    return RNVoxeetConferencekit.initialize(consumerKey, consumerSecret);
  }

  connect(userInfo: ConferenceUser): Promise {
    return RNVoxeetConferencekit.connect(userInfo);
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
    if(Platform.os == "android") {
      RNVoxeetConferencekit.screenAutoLock(activate);
    }
  }

  //deprecated
  isUserLoggedIn(): Promise<boolean> {
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
    return RNVoxeetConferencekit.connect(userInfo);
  }

  closeSession(): Promise {
    return RNVoxeetConferencekit.disconnect();
  }
}

module.exports = new VoxeetSDK();
