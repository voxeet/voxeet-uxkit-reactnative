import { ConferenceStatusUpdatedEvent, PermissionRefusedEvent, CameraSwitchSuccessEvent, CameraSwitchErrorEvent, QualityIndicators, RecordingStatusUpdatedEvent, ConferenceDestroyedPush, ConferenceEnded } from "./events/ConferenceStatusUpdatedEvent";
import { FilePresentationConverted, FilePresentationStarted, FilePresentationStopped, FilePresentationUpdated } from "./events/FilePresentationEvents";
import { VideoPresentationSeek, VideoPresentationPlay, VideoPresentationStopped, VideoPresentationPaused, VideoPresentationStarted } from "./events/VideoPresentationEvents";
import { ParticipantAddedEvent, ParticipantUpdatedEvent, StreamAddedEvent, StreamRemovedEvent, ConferenceParticipantQualityUpdatedEvent, StreamUpdatedEvent } from "./events/ConferenceUsersEvent";
interface EventMap {
    ["ConferenceStatusUpdatedEvent"]: ConferenceStatusUpdatedEvent;
    ["VideoPresentationSeek"]: VideoPresentationSeek;
    ["VideoPresentationPlay"]: VideoPresentationPlay;
    ["VideoPresentationStopped"]: VideoPresentationStopped;
    ["VideoPresentationPaused"]: VideoPresentationPaused;
    ["VideoPresentationStarted"]: VideoPresentationStarted;
    ["PermissionRefusedEvent"]: PermissionRefusedEvent;
    ["CameraSwitchSuccessEvent"]: CameraSwitchSuccessEvent;
    ["CameraSwitchErrorEvent"]: CameraSwitchErrorEvent;
    ["QualityIndicators"]: QualityIndicators;
    ["RecordingStatusUpdatedEvent"]: RecordingStatusUpdatedEvent;
    ["ConferenceDestroyedPush"]: ConferenceDestroyedPush;
    ["ConferenceEnded"]: ConferenceEnded;
    ["FilePresentationConverted"]: FilePresentationConverted;
    ["FilePresentationStarted"]: FilePresentationStarted;
    ["FilePresentationStopped"]: FilePresentationStopped;
    ["FilePresentationUpdated"]: FilePresentationUpdated;
    ["VideoPresentationSeek"]: VideoPresentationSeek;
    ["VideoPresentationPlay"]: VideoPresentationPlay;
    ["VideoPresentationStopped"]: VideoPresentationStopped;
    ["VideoPresentationPaused"]: VideoPresentationPaused;
    ["VideoPresentationStarted"]: VideoPresentationStarted;
    ["ParticipantAddedEvent"]: ParticipantAddedEvent;
    ["ParticipantUpdatedEvent"]: ParticipantUpdatedEvent;
    ["StreamAddedEvent"]: StreamAddedEvent;
    ["StreamUpdatedEvent"]: StreamUpdatedEvent;
    ["StreamRemovedEvent"]: StreamRemovedEvent;
    ["ConferenceParticipantQualityUpdatedEvent"]: ConferenceParticipantQualityUpdatedEvent;
}
export default class VoxeetEvents {
    private events;
    constructor();
    addListener<K extends keyof EventMap>(type: K, listener: (event: EventMap[K]) => void): void;
    removeListener<K extends keyof EventMap>(type: K, listener: (event: EventMap[K]) => void): void;
}
export {};
