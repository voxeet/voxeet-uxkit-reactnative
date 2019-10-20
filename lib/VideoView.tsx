//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?


import React, {Component} from 'react';
import { requireNativeComponent, findNodeHandle, UIManager } from 'react-native';

import MediaStream from "./types/MediaStream";

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
 * - attach: MediaStream
 * - cornerRadius: number
 * - isCircle: boolean
 * - hasFlip: boolean
 * - isAutoUnAttach: boolean
 * - scaleType: 'fit' | 'fill'
 */
export default class VideoView extends Component<Props, State> {
  static defaultProps = {
    isCircle: false,
    hasFlip: false,
    isAutoUnAttach: true,
    scaleType: 'fill'
  }

  _UiManager:any = UIManager;

  _videoView: React.Component|null;
  _videoViewHandler: null | number;
  _nextRequestId = 1;
  _requestMap: Map<number, Holder> = new Map();

  constructor(props: Props) {
    super(props);
    this._videoViewHandler = null;
    this._videoView = null;
  }

  componentDidMount() {
    this._videoViewHandler = findNodeHandle(this._videoView);
  }

  //android
  isAttached() {
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached);
  }

  //android
  isScreenShare() {
    return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isScreenShare);
  }

  _sendCallReturn(command: any) {
    const requestId: number = this._nextRequestId++;
    const requestMap: Map<number, Holder> = this._requestMap;

    const promise = new Promise((resolve, reject) => {
      requestMap[requestId] = { resolve, reject };
    });

    this._UiManager.dispatchViewManagerCommand(
      this._videoViewHandler,
      command,
      [ requestId ]
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
        ref={(v: React.Component) => this._videoView = v}
        { ...{onCallReturn: (event :any) => this._onCallReturn(event)} }
      />
    );
  }
}