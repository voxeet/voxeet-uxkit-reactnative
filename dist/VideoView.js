//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?
import React, { Component } from 'react';
import { requireNativeComponent, findNodeHandle, UIManager } from 'react-native';
import VoxeetSDK from './VoxeetSDK';
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
        this._requestMap = new Map();
        this._onCallReturn = (event) => {
            !!event.nativeEvent && this._onEvent(event.nativeEvent);
        };
        this._onEvent = (event) => {
            console.warn("event is", event);
            if (!event)
                return;
            const { requestId, error, message, peerId, streamId, attach, isAttached } = event;
            const promise = this._requestMap[requestId];
            this._requestMap.delete(requestId);
            if (error && message) {
                promise.reject(`${error} ${message}`);
            }
            else {
                promise.resolve(event);
            }
        };
        this._videoViewHandler = null;
        this._videoView = null;
    }
    componentDidMount() {
        VoxeetSDK.events.addListener("VoxeetConferencekitVideoView", this._onEvent);
        this._videoViewHandler = findNodeHandle(this._videoView);
    }
    componentWillUnmount() {
        VoxeetSDK.events.removeListener("VoxeetConferencekitVideoView", this._onEvent);
    }
    attach(participant, mediaStream) {
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.attach, participant.participantId, mediaStream.streamId).then(() => { });
    }
    unattach() {
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.unattach).then(() => { });
    }
    isAttached() {
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached)
            .then(r => !!r.isAttached);
    }
    isScreenShare() {
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isScreenShare)
            .then(r => !!r.isScreenShare);
    }
    _sendCallReturn(command, param1, param2) {
        const requestId = VideoView._nextRequestId++;
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
VideoView._nextRequestId = 1;
//# sourceMappingURL=VideoView.js.map