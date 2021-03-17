//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?
import React, { Component } from 'react';
import { requireNativeComponent, findNodeHandle, UIManager } from 'react-native';
const RCTVoxeetVideoView = requireNativeComponent('RCTVoxeetVideoView');
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
export default class VideoView extends Component {
    constructor(props) {
        super(props);
        this._UiManager = UIManager;
        this._nextRequestId = 1;
        this._requestMap = new Map();
        this._onCallReturn = (event) => {
            const { requestId, result, error } = event.nativeEvent;
            const promise = this._requestMap[requestId];
            if (result) {
                promise.resolve(result);
            }
            else {
                promise.reject(error);
            }
            this._requestMap.delete(requestId);
        };
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
    _sendCallReturn(command) {
        const requestId = this._nextRequestId++;
        const requestMap = this._requestMap;
        const promise = new Promise((resolve, reject) => {
            requestMap[requestId] = { resolve, reject };
        });
        this._UiManager.dispatchViewManagerCommand(this._videoViewHandler, command, [requestId]);
        return promise;
    }
    render() {
        return (<RCTVoxeetVideoView {...this.props} ref={(v) => this._videoView = v} {...{ onCallReturn: (event) => this._onCallReturn(event) }}/>);
    }
}
VideoView.defaultProps = {
    isCircle: false,
    hasFlip: false,
    isAutoUnAttach: true,
    scaleType: 'fill'
};
//# sourceMappingURL=VideoView.js.map