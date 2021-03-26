import Participant from "../types/Participant";
import MediaStream from "../types/MediaStream";

export interface ParticipantAddedEvent {
  user: Participant
}

export interface ParticipantUpdatedEvent {
  user: Participant
}

export interface StreamAddedEvent {
  user: Participant,
  mediaStream: MediaStream
}

export interface StreamRemovedEvent {
  user: Participant,
  mediaStream: MediaStream
}

export interface ConferenceParticipantQualityUpdatedEvent {
  user: Participant
}