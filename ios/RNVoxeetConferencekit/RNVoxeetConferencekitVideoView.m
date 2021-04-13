//
//  RNVoxeetConferencekitVideoView.m
//  RNVoxeetConferencekitVideoView
//
//  Created by Kévin Le Perf on 03/22/21.
//  Copyright © 2021 Voxeet. All rights reserved.
//

@import VoxeetSDK;
@import VoxeetUXKit;
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>

@interface RNVideoViewManager : RCTViewManager

@property (nonatomic, copy) NSMutableDictionary* refsIsScreenShare;
@property (nonatomic, copy) NSMutableDictionary* refsIsAttached;


@end

@implementation RNVideoViewManager

RCT_EXPORT_MODULE(RCTVoxeetVideoView);

- (UIView *)view
{
  return [[VTVideoView alloc] init];
}

RCT_EXPORT_METHOD(attach: (nonnull NSNumber *)reactTag
                  requestId: (nonnull NSNumber*) requestId
                  peerId: (NSString*) peerId
                  streamId: (NSString*) streamId) {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTVideoView *view = (VTVideoView *)[self.bridge.uiManager viewForReactTag: reactTag];
        if (!view) {
            [self sendError:view reactTag:reactTag requestId:requestId];
            return;
        }

        NSDictionary *userInfo = @{
            @"peerId": peerId ? peerId : @"",
            @"requestId": requestId,
            @"streamId": streamId ? streamId : @"",
            @"attach": @(false),
        };

        [[NSNotificationCenter defaultCenter] postNotificationName:
                               @"VoxeetConferencekitVideoView" object:nil userInfo:userInfo];
    });
}

RCT_EXPORT_METHOD(unattach: (nonnull NSNumber *)reactTag
                  requestId: (nonnull NSNumber*) requestId
                  peerId: (NSString*) peerId
                  streamId: (NSString*) streamId) {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTVideoView *view = (VTVideoView *)[self.bridge.uiManager viewForReactTag: reactTag];
        if (!view) {
            [self sendError:view reactTag:reactTag requestId:requestId];
            return;
        }
        
        [VoxeetSDK.shared.mediaDevice unattachMediaStream:nil renderer:view];
        
        
        NSDictionary *userInfo = @{
            @"peerId": peerId ? peerId : @"",
            @"requestId": requestId,
            @"streamId": streamId ? streamId : @"",
            @"attach": @(false),
        };

        [[NSNotificationCenter defaultCenter] postNotificationName:
                               @"VoxeetConferencekitVideoView" object:nil userInfo:userInfo];
    });
}

RCT_EXPORT_METHOD(isAttached: (nonnull NSNumber *)reactTag
                  requestId: (nonnull NSNumber*) requestId) {
    dispatch_async(dispatch_get_main_queue(), ^{
        
        if (!self.refsIsAttached) {
            self.refsIsAttached = [[NSMutableDictionary alloc] init];
        }

        VTVideoView *view = (VTVideoView *)[self.bridge.uiManager viewForReactTag: reactTag];
        if (!view) {
            [self sendError:view reactTag:reactTag requestId:requestId];
            return;
        }
        
        BOOL isAttached = [self.refsIsAttached valueForKey:[NSValue valueWithNonretainedObject:view]];
        
        NSDictionary *userInfo = @{
            @"requestId": requestId,
            @"isAttached": @(isAttached),
        };

        [[NSNotificationCenter defaultCenter] postNotificationName:
                               @"VoxeetConferencekitVideoView" object:nil userInfo:userInfo];
    });
}

RCT_EXPORT_METHOD(isScreenShare: (nonnull NSNumber *)reactTag
                  requestId: (nonnull NSNumber*) requestId) {
    dispatch_async(dispatch_get_main_queue(), ^{
        
        if (!self.refsIsScreenShare) {
            self.refsIsScreenShare = [[NSMutableDictionary alloc] init];
        }

        VTVideoView *view = (VTVideoView *)[self.bridge.uiManager viewForReactTag: reactTag];
        if (!view) {
            [self sendError:view reactTag:reactTag requestId:requestId];
            return;
        }
        
        BOOL isScreenShare = [self.refsIsScreenShare valueForKey:[NSValue valueWithNonretainedObject:view]];
        
        NSDictionary *userInfo = @{
            @"requestId": requestId,
            @"isScreenShare": @(isScreenShare),
        };

        [[NSNotificationCenter defaultCenter] postNotificationName:
                               @"VoxeetConferencekitVideoView" object:nil userInfo:userInfo];
    });
}

- (void) sendError: (VTVideoView *)view
          reactTag: (NSNumber *)reactTag
         requestId: (nonnull NSNumber*) requestId {
    NSDictionary *userInfo = @{
        @"requestId": requestId,
        @"error": @"ERROR_INVALID_REACT_TAG",
        @"message": [NSString stringWithFormat: @"ReactTag passed: %@", reactTag],
    };

    [[NSNotificationCenter defaultCenter] postNotificationName:
                           @"VoxeetConferencekitVideoView" object:nil userInfo:userInfo];
}

@end
