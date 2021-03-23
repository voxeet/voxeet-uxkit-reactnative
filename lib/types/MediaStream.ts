export enum MediaStreamType {
  Camera = "Camera",
  ScreenShare = "ScreenShare",
  Custom = "Custom"
}

export default interface MediaStream {
  peerId: string;
  streamId: string;
  hasVideoTracks: boolean;
  hasAudioTracks: boolean;
  type: MediaStreamType;
}