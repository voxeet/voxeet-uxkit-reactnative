export type MediaStreamType = "Camera" | "ScreenShare" | "Custom";

export default interface MediaStream {
  peerId: string;
  streamId: string;
  hasVideoTracks: boolean;
  hasAudioTracks: boolean;
  type: MediaStreamType;
}