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

@interface RNVVideoViewManager : RCTViewManager

@property (nonatomic, copy) NSMutableDictionary* refsIsScreenShare;
@property (nonatomic, copy) NSMutableDictionary* refsIsAttached;

@end

@implementation RNVVideoViewManager

RCT_EXPORT_MODULE(RCTVoxeetVideoView);

- (UIView *)view
{
  return [[VTVideoView alloc] init];
}

RCT_EXPORT_METHOD(attach: (nonnull NSNumber *)reactTag
                  peerId: (NSString*) peerId
                  streamId: (NSString*) streamId
                  resolver: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        //TODO
        resolve(nil);
    });
}

RCT_EXPORT_METHOD(unattach: (nonnull NSNumber *)reactTag
                  peerId: (NSString*) peerId
                  streamId: (NSString*) streamId
                  resolver: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        //TODO
        resolve(nil);
    });
}

RCT_EXPORT_METHOD(isAttached: (nonnull NSNumber *)reactTag resolver: (RCTPromiseResolveBlock)resolve rejecter: (RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        
        if (!self.refsIsAttached) {
            self.refsIsAttached = [[NSMutableDictionary alloc] init];
        }

        VTVideoView *view = (VTVideoView *)[self.bridge.uiManager viewForReactTag: reactTag];
        if (!view) {
            reject(@"ERROR_INVALID_REACT_TAG", [NSString stringWithFormat: @"ReactTag passed: %@", reactTag], nil);
            return;
        }
        
        BOOL isAttached = [self.refsIsAttached valueForKey:[NSValue valueWithNonretainedObject:view]];
        resolve(@(isAttached));
    });
}

RCT_EXPORT_METHOD(isScreenShare: (nonnull NSNumber *)reactTag resolver: (RCTPromiseResolveBlock)resolve rejecter: (RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        
        if (!self.refsIsScreenShare) {
            self.refsIsScreenShare = [[NSMutableDictionary alloc] init];
        }

        VTVideoView *view = (VTVideoView *)[self.bridge.uiManager viewForReactTag: reactTag];
        if (!view) {
            reject(@"ERROR_INVALID_REACT_TAG", [NSString stringWithFormat: @"ReactTag passed: %@", reactTag], nil);
            return;
        }
        
        BOOL isScreenShare = [self.refsIsScreenShare valueForKey:[NSValue valueWithNonretainedObject:view]];
        resolve(@(isScreenShare));
    });
}

@end
