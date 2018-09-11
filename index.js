/*
import { NativeModules } from 'react-native';

const { RNReactNativeVoxeetConferencekit } = NativeModules;

export default RNReactNativeVoxeetConferencekit;*/

import { NativeModules } from 'react-native';
const { RNReactNativeVoxeetConferencekit } = NativeModules;

export type ConferenceUser = {
  id: string,
  name: string,
  avatar: string,
};

class VoxeetSDK {
  initialize(consumerKey:string, consumerSecret:string) {
    RNReactNativeVoxeetConferencekit.initialize(consumerKey, consumerSecret);
  }

  openSession(userId: string, name: string, avatarURL: string): Promise {
    return RNReactNativeVoxeetConferencekit.openSession(userId, name, avatarURL);
  }

  appearMaximized(activate: boolean) {
    RNReactNativeVoxeetConferencekit.appearMaximized(activate);
  }

  defaultBuiltInSpeaker(activate: boolean) {
    RNReactNativeVoxeetConferencekit.defaultBuiltInSpeaker(activate);
  }

  screenAutoLock(activate: boolean) {
    RNReactNativeVoxeetConferencekit.screenAutoLock(activate);
  }

  startConference(conferenceId: string, participants: Array<ConferenceUser>, invite: boolean): Promise {
    return RNReactNativeVoxeetConferencekit.startConference(conferenceId, participants, invite);
  }

  initialize(consumerKey:string, consumerSecret:string) {
    RNReactNativeVoxeetConferencekit.initialize(consumerKey, consumerSecret);
  }

  openSession(userId: string, name: string, avatarURL: string): Promise {
    return RNReactNativeVoxeetConferencekit.openSession(userId, name, avatarURL);
  }

  appearMaximized(activate: boolean) {
    RNReactNativeVoxeetConferencekit.appearMaximized(activate);
  }

  defaultBuiltInSpeaker(activate: boolean) {
    RNReactNativeVoxeetConferencekit.defaultBuiltInSpeaker(activate);
  }

  screenAutoLock(activate: boolean) {
    RNReactNativeVoxeetConferencekit.screenAutoLock(activate);
  }

  startConference(conferenceId: string, participants: Array<ConferenceUser>, invite: boolean): Promise {
    return RNReactNativeVoxeetConferencekit.startConference(conferenceId, participants, invite);
  }
}

module.exports = new VoxeetSDK();
