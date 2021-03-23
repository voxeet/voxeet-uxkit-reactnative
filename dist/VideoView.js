//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?
import React, { Component } from 'react';
import { requireNativeComponent, findNodeHandle, UIManager, Platform, NativeModules } from 'react-native';
const RCTVoxeetVideoView = requireNativeComponent('RCTVoxeetVideoView');
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
    attach(participant, mediaStream) {
        if (Platform.OS == "ios") {
            return NativeModules.RCTVoxeetVideoView.attach(this._videoViewHandler, participant.participantId, mediaStream.streamId);
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.attach, participant.participantId, mediaStream.streamId);
    }
    unattach() {
        if (Platform.OS == "ios") {
            return NativeModules.RCTVoxeetVideoView.unattach(this._videoViewHandler);
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.unattach);
    }
    isAttached() {
        if (Platform.OS == "ios") {
            return NativeModules.RCTVoxeetVideoView.isAttached(this._videoViewHandler);
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached);
    }
    isScreenShare() {
        if (Platform.OS == "ios") {
            return NativeModules.RCTVoxeetVideoView.isScreenShare(this._videoViewHandler);
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isScreenShare);
    }
    _sendCallReturn(command, param1, param2) {
        const requestId = this._nextRequestId++;
        const requestMap = this._requestMap;
        const promise = new Promise((resolve, reject) => {
            requestMap[requestId] = { resolve, reject };
        });
        this._UiManager.dispatchViewManagerCommand(this._videoViewHandler, command, [requestId, param1, param2]);
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