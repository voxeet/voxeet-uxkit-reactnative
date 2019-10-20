//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
import React, { Component } from 'react';
import { requireNativeComponent, findNodeHandle, UIManager } from 'react-native';
var RCTVoxeetVideoView = requireNativeComponent('RCTVoxeetVideoView');
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
var VideoView = /** @class */ (function (_super) {
    __extends(VideoView, _super);
    function VideoView(props) {
        var _this = _super.call(this, props) || this;
        _this._UiManager = UIManager;
        _this._nextRequestId = 1;
        _this._requestMap = new Map();
        _this._onCallReturn = function (event) {
            var _a = event.nativeEvent, requestId = _a.requestId, result = _a.result, error = _a.error;
            var promise = _this._requestMap[requestId];
            if (result) {
                promise.resolve(result);
            }
            else {
                promise.reject(error);
            }
            _this._requestMap.delete(requestId);
        };
        _this._videoViewHandler = null;
        _this._videoView = null;
        return _this;
    }
    VideoView.prototype.componentDidMount = function () {
        this._videoViewHandler = findNodeHandle(this._videoView);
    };
    //android
    VideoView.prototype.isAttached = function () {
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isAttached);
    };
    //android
    VideoView.prototype.isScreenShare = function () {
        return this._sendCallReturn(this._UiManager.RCTVoxeetVideoView.Commands.isScreenShare);
    };
    VideoView.prototype._sendCallReturn = function (command) {
        var requestId = this._nextRequestId++;
        var requestMap = this._requestMap;
        var promise = new Promise(function (resolve, reject) {
            requestMap[requestId] = { resolve: resolve, reject: reject };
        });
        this._UiManager.dispatchViewManagerCommand(this._videoViewHandler, command, [requestId]);
        return promise;
    };
    VideoView.prototype.render = function () {
        var _this = this;
        return (<RCTVoxeetVideoView {...this.props} ref={function (v) { return _this._videoView = v; }} {...{ onCallReturn: function (event) { return _this._onCallReturn(event); } }}/>);
    };
    VideoView.defaultProps = {
        isCircle: false,
        hasFlip: false,
        isAutoUnAttach: true,
        scaleType: 'fill'
    };
    return VideoView;
}(Component));
export default VideoView;
//# sourceMappingURL=VideoView.js.map