export declare enum RTCPMode {
    WORST = "worst",
    BEST = "best"
}
export declare enum Mode {
    STANDARD = "standard",
    PUSH = "push"
}
export declare enum Codec {
    VP8 = "VP8",
    H264 = "H264"
}
export interface CreateParameters {
    ttl?: number;
    rtcpMode?: RTCPMode;
    mode?: Mode;
    videoCodec?: Codec;
    liveRecording?: boolean;
}
export default interface CreateOptions {
    alias?: string;
    params?: CreateParameters;
}
