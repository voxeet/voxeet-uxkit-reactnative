
export interface VideoPresentationSeek {
  conferenceId: string,
  key: string,
  participantId: string,
  timestamp: number
}

export interface VideoPresentationPlay {
  conferenceId: string,
  key: string,
  participantId: string
}

export interface VideoPresentationStopped {
  conferenceId: string,
  key: string,
  participantId: string
}

export interface VideoPresentationPaused {
  conferenceId: string,
  key: string,
  participantId: string,
  timestamp: number
}

export interface VideoPresentationStarted {
  conferenceId: string,
  key: string,
  participantId: string,
  timestamp: number,
  url: string
}