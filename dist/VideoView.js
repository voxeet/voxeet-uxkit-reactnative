//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?
import React, { Component } from 'react';
import { requireNativeComponent, findNodeHandle, UIManager, Platform, View } from 'react-native';
import VoxeetSDK from './VoxeetSDK';
const RCTVoxeetVideoView = requireNativeComponent('RCTVoxeetVideoView');
const ValidProps = ["style", "isMirror", "scaleType"];
/**
 * Composes `View`.
 *
 * - isMirror: boolean
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
        this.state = {};
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
            if (error && message && promise) {
                promise.reject(`${error} ${message}`);
            }
            else if (promise) {
                promise.resolve(event);
            }
        };
        this._videoViewHandler = null;
        this._videoView = null;
    }
    filteredProps() {
        const result = {};
        Object.keys(this.props).filter(k => ValidProps).forEach(k => result[k] = this.props[k]);
        return result;
    }
    componentDidMount() {
        VoxeetSDK.events.addListener("VoxeetConferencekitVideoView", this._onEvent);
    }
    componentWillUnmount() {
        VoxeetSDK.events.removeListener("VoxeetConferencekitVideoView", this._onEvent);
    }
    attach(participant, mediaStream) {
        if (Platform.OS == "android") {
            this.setState({ mediaStream });
            return Promise.resolve();
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.attach, participant.participantId, mediaStream.streamId).then(() => { });
    }
    unattach() {
        if (Platform.OS == "android") {
            this.setState({ mediaStream: undefined });
            return Promise.resolve();
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.unattach).then(() => { });
    }
    isAttached() {
        if (Platform.OS == "android") {
            if (!this._videoView || !this._videoViewHandler)
                return Promise.resolve(false);
            if (!this.state.mediaStream)
                return Promise.resolve(false);
        }
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached)
            .then(r => !!r.isAttached);
    }
    isScreenShare() {
        if (Platform.OS == "android") {
            if (!this._videoView || !this._videoViewHandler)
                return Promise.resolve(false);
            if (!this.state.mediaStream)
                return Promise.resolve(false);
        }
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
    setVideoView(v) {
        if (!!v) {
            this._videoView = v;
            this._videoViewHandler = findNodeHandle(this._videoView);
        }
        else {
            this._videoView = null;
            this._videoViewHandler = null;
        }
    }
    render() {
        const props = this.filteredProps();
        if (Platform.OS == "android") {
            if (!this.state.mediaStream) {
                return <View {...props}/>;
            }
            else {
                return (<RCTVoxeetVideoView {...props} attach={this.state.mediaStream} ref={(v) => this.setVideoView(v)} {...{ onCallReturn: (event) => this._onCallReturn(event) }}/>);
            }
        }
        return (<RCTVoxeetVideoView {...props} ref={(v) => this._videoView = v} {...{ onCallReturn: (event) => this._onCallReturn(event) }}/>);
    }
}
VideoView.defaultProps = {
    isCircle: false,
    isMirror: false,
    hasFlip: false,
    isAutoUnAttach: true,
    scaleType: 'fill'
};
VideoView._nextRequestId = 1;
//# sourceMappingURL=VideoView.js.map