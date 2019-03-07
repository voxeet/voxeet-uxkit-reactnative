export enum RTCPMode {
  WORST = "worst",
  BEST = "best"
}
  
export enum Mode {
  STANDARD = "standard",
  PUSH = "push"
}
  
export enum Codec {
  VP8 = "VP8",
  H264 = "H264"
}

export interface CreateParameters {
  ttl?: number;
  rtcpMode?: RTCPMode; //best / worst, default => worst
  mode?: Mode; // push / standard, default => standard
  videoCodec?: Codec; //default VP8
  liveRecording?: boolean; //default false
}

export default interface CreateOptions {
  alias?: string;
  params?: CreateParameters;
}