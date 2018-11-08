/*
import { NativeModules } from 'react-native';

const { RNReactNativeVoxeetConferencekit } = NativeModules;

export default RNReactNativeVoxeetConferencekit;*/

import { NativeModules, Platform } from 'react-native';
const { RNReactNativeVoxeetConferencekit } = NativeModules;

export type ConferenceUser = {
  externalId: string,
  name: string,
  avatar: string
};

class VoxeetSDK {

  initialize(consumerKey: string, consumerSecret: string): Promise {
    return RNReactNativeVoxeetConferencekit.initialize(consumerKey, consumerSecret);
  }

  connect(userInfo: ConferenceUser): Promise {
    return RNReactNativeVoxeetConferencekit.openSession(userInfo);
  }

  disconnect(): Promise {
    return RNReactNativeVoxeetConferencekit.disconnect();
  }

  create(parameters: any): Promise {
    return RNReactNativeVoxeetConferencekit.create(parameters);
  }

  join(conferenceId: string): Promise {
    return RNReactNativeVoxeetConferencekit.join(conferenceId);
  }

  leave(): Promise {
    return RNReactNativeVoxeetConferencekit.leave();
  }

  invite(conferenceId: string, participants: ConferenceUser[]): Promise {
    return RNReactNativeVoxeetConferencekit.invite(conferenceId, participants);
  }

  sendBroadcastMessage(message: string): Promise {
    return RNReactNativeVoxeetConferencekit.sendBroadcastMessage(message);
  }

  appearMaximized(activate: boolean) {
    RNReactNativeVoxeetConferencekit.appearMaximized(activate);
  }

  screenAutoLock(activate: boolean) {
    RNReactNativeVoxeetConferencekit.screenAutoLock(activate);
  }

  defaultBuiltInSpeaker(activate: boolean) {
    RNReactNativeVoxeetConferencekit.defaultBuiltInSpeaker(activate);
  }

  defaultVideo(enabled: boolean) {
    RNReactNativeVoxeetConferencekit.defaultVideo(enabled);
  }

  /*
   *  Android methods
   */

  screenAutoLock(activate: boolean) {
    if(Platform.os == "android") {
      RNReactNativeVoxeetConferencekit.screenAutoLock(activate);
    }
  }

  //deprecated
  isUserLoggedIn(): boolean {
    return RNReactNativeVoxeetConferencekit.isUserLoggedIn();
  }

  checkForAwaitingConference(): Promise {
    if(Platform.os != "android") return new Promise(r => r());

    return RNReactNativeVoxeetConferencekit.checkForAwaitingConference();
  }

  /*
   *  Deprecated methods
   */
  startConference(conferenceId: string, participants: Array<ConferenceUser>): Promise {
    return RNReactNativeVoxeetConferencekit.startConference(conferenceId, participants);
  }

  stopConference(): Promise {
    return RNReactNativeVoxeetConferencekit.leave();
  }

  openSession(userInfo: ConferenceUser): Promise {
    return RNReactNativeVoxeetConferencekit.openSession(userInfo);
  }

  closeSession(): Promise {
    return RNReactNativeVoxeetConferencekit.closeSession();
  }

}

module.exports = new VoxeetSDK();
