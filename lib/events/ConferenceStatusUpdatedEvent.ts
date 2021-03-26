export type ConferenceStatus =
  "DEFAULT" |
  "CREATING" |
  "CREATED" |
  "JOINING" |
  "JOINED" |
  /** @deprecated */
  "FIRST_PARTICIPANT" |
  /** @deprecated */
  "NO_MORE_PARTICIPANT" |
  "LEAVING" |
  "LEFT" |
  "ERROR" |
  "DESTROYED" |
  "ENDED";

export type PermissionRefusedType = "CAMERA" | "MICROPHONE";

export interface ConferenceStatusUpdatedEvent {
  conferenceId: string,
  conferenceAlias: string,
  status: ConferenceStatus
}

export interface PermissionRefusedEvent {
  permission: PermissionRefusedType
}

export interface CameraSwitchSuccessEvent {
  isFront: boolean
}

export interface CameraSwitchErrorEvent {
  message: string
}

export interface QualityIndicators {
  mos: number
}

export interface RecordingStatusUpdatedEvent {
  conferenceId: string,
  participantId: string,
  recordingStatus: string,
}

export interface ConferenceDestroyedPush {
  conferenceId: string
}

export interface ConferenceEnded {
  conferenceId: string
}

