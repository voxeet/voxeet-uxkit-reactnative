import { NativeModules, NativeEventEmitter } from 'react-native';
import { ConferenceStatusUpdatedEvent } from "./events/ConferenceStatusUpdatedEvent";
import { FilePresentationConverted,
  FilePresentationStarted,
  FilePresentationStopped,
  FilePresentationUpdated
 } from "./events/FilePresentationEvents";
 import { VideoPresentationSeek,
  VideoPresentationPlay,
  VideoPresentationStopped,
  VideoPresentationPaused,
  VideoPresentationStarted
 } from "./events/VideoPresentationEvents";
const { RNVoxeetConferencekit } = NativeModules;

interface EventMap {
  ["ConferenceStatusUpdatedEvent"]: ConferenceStatusUpdatedEvent;

  ["FilePresentationConverted"]: FilePresentationConverted;
  ["FilePresentationStarted"]: FilePresentationStarted;
  ["FilePresentationStopped"]: FilePresentationStopped;
  ["FilePresentationUpdated"]: FilePresentationUpdated;

  ["VideoPresentationSeek"]: VideoPresentationSeek;
  ["VideoPresentationPlay"]: VideoPresentationPlay;
  ["VideoPresentationStopped"]: VideoPresentationStopped;
  ["VideoPresentationPaused"]: VideoPresentationPaused;
  ["VideoPresentationStarted"]: VideoPresentationStarted;
}

export default class VoxeetEvents {
  private events = new NativeEventEmitter(RNVoxeetConferencekit);

  constructor() {

  }

  public addListener<K extends keyof EventMap>(
    type: K,
    listener: (event: EventMap[K]) => void
  ): void {
    this.events.addListener(type, listener);
  }

  public removeListener<K extends keyof EventMap>(
    type: K,
    listener: (event: EventMap[K]) => void
  ): void {
    this.events.removeListener(type, listener);
  }
}
