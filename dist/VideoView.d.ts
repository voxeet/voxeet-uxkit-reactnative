import { Component } from 'react';
import MediaStream from "./types/MediaStream";
import Participant from './types/Participant';
export interface Stream {
    peerId: string;
    label: string;
}
export interface State {
}
export interface Props {
    attach: MediaStream | undefined;
    cornerRadius: number;
    isCircle: boolean;
    hasFlip: boolean;
    isAutoUnAttach: boolean;
    scaleType: string;
}
export interface Resolve {
    (result: any): any | undefined;
}
export interface Reject {
    (error: Error): any | undefined;
}
export interface Holder {
    resolve: Resolve;
    reject: Reject;
}
interface VideoViewAsyncCallResult {
    requestId: number;
    error?: string;
    message?: string;
    peerId?: string;
    streamId?: string;
    attach?: number;
    isAttached?: number;
    isScreenShare?: number;
}
/**
 * Composes `View`.
 *
 * - cornerRadius: number
 * - isCircle: boolean
 * - hasFlip: boolean
 * - isAutoUnAttach: boolean
 * - scaleType: 'fit' | 'fill'
 *
 *
 * Public methods :
 *
 * attach(participant: Participant, mediaStream: MediaStream): Promise<void>
 * unattach(): Promise<void>
 * isAttached(): Promise<boolean>
 * isScreenShare(): Promise<boolean>
 */
export default class VideoView extends Component<Props, State> {
    static defaultProps: {
        isCircle: boolean;
        hasFlip: boolean;
        isAutoUnAttach: boolean;
        scaleType: string;
    };
    private _UiManager;
    private _videoView;
    private _videoViewHandler;
    private static _nextRequestId;
    private _requestMap;
    constructor(props: Props);
    componentDidMount(): void;
    componentWillUnmount(): void;
    attach(participant: Participant, mediaStream: MediaStream): Promise<void>;
    unattach(): Promise<void>;
    isAttached(): Promise<boolean>;
    isScreenShare(): Promise<boolean>;
    _sendCallReturn(command: any, param1?: any, param2?: any): Promise<VideoViewAsyncCallResult>;
    _onCallReturn: (event: any) => void;
    _onEvent: (event: VideoViewAsyncCallResult) => void;
    render(): JSX.Element;
}
export {};
