//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?


import React, {Component} from 'react';
import { requireNativeComponent, findNodeHandle, UIManager, Platform, NativeModules, View } from 'react-native';

import MediaStream from "./types/MediaStream";
import Participant from './types/Participant';
import VoxeetSDK from './VoxeetSDK';

const RCTVoxeetVideoView = requireNativeComponent('RCTVoxeetVideoView');

export interface Stream {
  peerId: string;
  label: string;
}

export interface State {
  mediaStream?: MediaStream
}

export interface Props {
  attach: MediaStream|undefined;
  cornerRadius: number,
  isCircle: boolean,
  hasFlip: boolean,
  isAutoUnAttach: boolean,
  scaleType: "fit"|"fill"; //[ 'fit', 'fill' ]
}

export interface Resolve {
  (result: any): any|undefined;
}

export interface Reject {
  (error: Error): any|undefined;
}

export interface Holder {
  resolve: Resolve;
  reject: Reject;
}

interface VideoViewAsyncCallResult {
  requestId: number,
  error?: string,
  message?: string,
  peerId?: string,
  streamId?: string,
  attach?: number,
  isAttached?: number,
  isScreenShare?: number
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
  static defaultProps = {
    isCircle: false,
    hasFlip: false,
    isAutoUnAttach: true,
    scaleType: 'fill'
  }

  private _UiManager:any = UIManager;

  private _videoView: React.Component|null;
  private _videoViewHandler: null | number;
  private static _nextRequestId = 1;
  private _requestMap: Map<number, Holder> = new Map();

  constructor(props: Props) {
    super(props);
    this._videoViewHandler = null;
    this._videoView = null;
  }

  componentDidMount() {
    VoxeetSDK.events.addListener("VoxeetConferencekitVideoView", this._onEvent);
  }

  componentWillUnmount() {
    VoxeetSDK.events.removeListener("VoxeetConferencekitVideoView", this._onEvent);
  }

  attach(participant: Participant, mediaStream: MediaStream): Promise<void> {
    if(Platform.OS == "android") {
        this.setState({mediaStream});
        return Promise.resolve();
    }
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.attach, participant.participantId, mediaStream.streamId).then(() => {});
  }

  unattach(): Promise<void> {
    if(Platform.OS == "android") {
        this.setState({mediaStream: undefined});
        return Promise.resolve();
    }

    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.unattach).then(() => {});
  }

  isAttached(): Promise<boolean> {
    if(Platform.OS == "android") {
        if(!this._videoView || !this._videoViewHandler) return Promise.resolve(false);
        if(!this.state.mediaStream) return Promise.resolve(false);
    }

    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached)
    .then(r => !!r.isAttached);
  }

  isScreenShare(): Promise<boolean> {
    if(Platform.OS == "android") {
        if(!this._videoView || !this._videoViewHandler) return Promise.resolve(false);
        if(!this.state.mediaStream) return Promise.resolve(false);
    }

    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isScreenShare)
    .then(r => !!r.isScreenShare);
  }

  _sendCallReturn(command: any, param1?: any, param2?: any): Promise<VideoViewAsyncCallResult> {
    const requestId: number = VideoView._nextRequestId++;
    const requestMap: Map<number, Holder> = this._requestMap;

    const promise = new Promise<VideoViewAsyncCallResult>((resolve, reject) => {
      requestMap[requestId] = { resolve, reject };
    });

    this._UiManager.dispatchViewManagerCommand(
      this._videoViewHandler,
      command,
      [ requestId, param1, param2]
    );

    return promise;
  }

  _onCallReturn = (event: any) => {
    !!event.nativeEvent && this._onEvent(event.nativeEvent);
  }
  
  _onEvent = (event: VideoViewAsyncCallResult) => {
    console.warn("event is", event);
    if(!event) return;
    const { requestId, error, message, peerId, streamId, attach, isAttached} = event;
    const promise = this._requestMap[requestId];

    this._requestMap.delete(requestId);

    if(error && message && promise) {
        promise.reject(`${error} ${message}`);
    } else if(promise) {
        promise.resolve(event);
    }
  }

  private setVideoView(v: VideoView|null|undefined) {
    if(!!v) {
      this._videoView = v;
      this._videoViewHandler = findNodeHandle(this._videoView);
    } else {
      this._videoView = null;
      this._videoViewHandler = null;
    }
  }

  render() {
    if(Platform.OS == "android") {
      if(!this.state.mediaStream) {
          return <View {...this.props} />
      } else {
          return (
            <RCTVoxeetVideoView {...this.props}
              attach={this.state.mediaStream}
              ref={(v: React.Component|null) => this.setVideoView(v)}
              {...{ onCallReturn: (event: any) => this._onCallReturn(event) }}/>
          );
      }
    }

    return (
      <RCTVoxeetVideoView
        {...this.props}
        ref={(v: React.Component|null) => this._videoView = v}
        { ...{onCallReturn: (event: any) => this._onCallReturn(event)} }
      />
    );
  }
}