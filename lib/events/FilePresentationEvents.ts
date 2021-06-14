
export interface FilePresentationConverted {
  nbImageConverted: number,
  fileId: string,
  name: string,
  size: number
}

export interface FilePresentationStarted {
  conferenceId: string,
  fileId: string,
  participantId: string,
  position: number,
  imageCount: number
}

export interface FilePresentationStopped {
  conferenceId: string,
  fileId: string,
  participantId: string
}

export interface FilePresentationUpdated {
  conferenceId: string,
  fileId: string,
  participantId: string,
  position: number
}