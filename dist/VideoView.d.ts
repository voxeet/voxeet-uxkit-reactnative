import React, { Component } from 'react';
import MediaStream from "./types/MediaStream";
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
/**
 * Composes `View`.
 *
 * - attach: MediaStream
 * - cornerRadius: number
 * - isCircle: boolean
 * - hasFlip: boolean
 * - isAutoUnAttach: boolean
 * - scaleType: 'fit' | 'fill'
 */
export default class VideoView extends Component<Props, State> {
    static defaultProps: {
        isCircle: boolean;
        hasFlip: boolean;
        isAutoUnAttach: boolean;
        scaleType: string;
    };
    _UiManager: any;
    _videoView: React.Component | null;
    _videoViewHandler: null | number;
    _nextRequestId: number;
    _requestMap: Map<number, Holder>;
    constructor(props: Props);
    componentDidMount(): void;
    isAttached(): Promise<unknown>;
    isScreenShare(): Promise<unknown>;
    _sendCallReturn(command: any): Promise<unknown>;
    _onCallReturn: (event: any) => void;
    render(): JSX.Element;
}
