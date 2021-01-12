export enum RTCPMode {
  WORST = "worst",
  AVERAGE = "average",
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
  dolbyVoice?: boolean;
  simulcast?: boolean; //default false
}

export interface CreateOptions {
  alias?: string;
  params?: CreateParameters;
}

export interface CreateResult {
  conferenceId?: string;
  conferenceAlias?: string;
  isNew?: boolean
}
