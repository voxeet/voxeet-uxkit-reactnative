//promise call done using https://pspdfkit.com/blog/2018/advanced-techniques-for-react-native-ui-components/
//TODO lock call ?
import React, {Component} from 'react';
import PropTypes from 'prop-types';
import { requireNativeComponent, findNodeHandle, UIManager } from 'react-native';
import type { MediaStream } from "./VoxeetTypes";

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
class VideoView extends Component {
  static defaultProps = {
    isCircle: false,
    hasFlip: false,
    isAutoUnAttach: true,
    scaleType: 'fill'
  }

  _nextRequestId = 1;
  _requestMap = new Map();

  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.videoViewHandle = findNodeHandle(this.videoView);
  }

  isAttached() {
    return _sendCallReturn(UIManager.RCTVoxeetVideoView.Commands.isAttached);
  }

  isScreenShare() {
    return _sendCallReturn(UIManager.RCTVoxeetVideoView.Commands.isScreenShare);
  }

  _sendCallReturn(command) {
    const requestId = this._nextRequestId++;
    const requestMap = this._requestMap;

    const promise = new Promise((resolve, reject) => {
      requestMap[requestId] = { resolve: resolve, reject: reject };
    });

    UIManager.dispatchViewManagerCommand(
      this.videoViewHandle,
      command,
      [ requestId ]
    );

    return promise;
  }

  _onCallReturn = (event) => {
    const { requestId, result, error } = event.nativeEvent;
    const promise = this._requestMap[requestId];
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
        ref={v => this.videoView = v}
        onCallReturn={this._onCallReturn}
      />
    );
  }
}

VideoView.propTypes = {
  attach: PropTypes.shape({
    peerId: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired
  }),
  cornerRadius: PropTypes.number,
  isCircle: PropTypes.bool,
  hasFlip: PropTypes.bool,
  isAutoUnAttach: PropTypes.bool,
  scaleType: PropTypes.onOfTypes([ 'fit', 'fill'])
};

module.exports = VideoView;
