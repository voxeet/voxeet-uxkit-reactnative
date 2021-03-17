import { NativeModules, NativeEventEmitter } from 'react-native';
import ConferenceStatusUpdatedEvent from "./events/ConferenceStatusUpdatedEvent";
const { RNVoxeetConferencekit } = NativeModules;

interface EventMap {
  ["ConferenceStatusUpdatedEvent"]: ConferenceStatusUpdatedEvent;
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
