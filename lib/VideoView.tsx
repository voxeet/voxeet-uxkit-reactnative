//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?


import React, {Component} from 'react';
import { requireNativeComponent, findNodeHandle, UIManager, Platform, NativeModules } from 'react-native';

import MediaStream from "./types/MediaStream";
import Participant from './types/Participant';

const RCTVoxeetVideoView = requireNativeComponent('RCTVoxeetVideoView');

export interface Stream {
  peerId: string;
  label: string;
}

export interface State {

}

export interface Props {
  attach: MediaStream|undefined;
  cornerRadius: number,
  isCircle: boolean,
  hasFlip: boolean,
  isAutoUnAttach: boolean,
  scaleType: string; //[ 'fit', 'fill' ]
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
  private _nextRequestId = 1;
  private _requestMap: Map<number, Holder> = new Map();

  constructor(props: Props) {
    super(props);
    this._videoViewHandler = null;
    this._videoView = null;
  }

  componentDidMount() {
    this._videoViewHandler = findNodeHandle(this._videoView);
  }

  attach(participant: Participant, mediaStream: MediaStream): Promise<void> {
    if(Platform.OS == "ios") {
      return NativeModules.RCTVoxeetVideoView.attach(this._videoViewHandler, participant.participantId, mediaStream.streamId);
    }
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.attach, participant.participantId, mediaStream.streamId);
  }

  unattach(): Promise<void> {
    if(Platform.OS == "ios") {
      return NativeModules.RCTVoxeetVideoView.unattach(this._videoViewHandler);
    }
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.unattach);
  }

  isAttached(): Promise<boolean> {
    if(Platform.OS == "ios") {
      return NativeModules.RCTVoxeetVideoView.isAttached(this._videoViewHandler);
    }
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached);
  }

  isScreenShare(): Promise<boolean> {
    if(Platform.OS == "ios") {
      return NativeModules.RCTVoxeetVideoView.isScreenShare(this._videoViewHandler);
    }
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isScreenShare);
  }

  _sendCallReturn(command: any, param1?: any, param2?: any): Promise<any> {
    const requestId: number = this._nextRequestId++;
    const requestMap: Map<number, Holder> = this._requestMap;

    const promise = new Promise((resolve, reject) => {
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
    const { requestId, result, error } = event.nativeEvent;
    const promise: Holder = this._requestMap[requestId];
    if (result) {
      promise.resolve(result);
    } else {
      promise.reject(error);
    }
    this._requestMap.delete(requestId);
  }

  render() {
    return (
      <RCTVoxeetVideoView
        {...this.props}
        ref={(v: React.Component|null) => this._videoView = v}
        { ...{onCallReturn: (event :any) => this._onCallReturn(event)} }
      />
    );
  }
}