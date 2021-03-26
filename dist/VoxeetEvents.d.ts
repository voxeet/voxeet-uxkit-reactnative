import { ConferenceStatusUpdatedEvent } from "./events/ConferenceStatusUpdatedEvent";
import { FilePresentationConverted, FilePresentationStarted, FilePresentationStopped, FilePresentationUpdated } from "./events/FilePresentationEvents";
import { VideoPresentationSeek, VideoPresentationPlay, VideoPresentationStopped, VideoPresentationPaused, VideoPresentationStarted } from "./events/VideoPresentationEvents";
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
    private events;
    constructor();
    addListener<K extends keyof EventMap>(type: K, listener: (event: EventMap[K]) => void): void;
    removeListener<K extends keyof EventMap>(type: K, listener: (event: EventMap[K]) => void): void;
}
export {};
