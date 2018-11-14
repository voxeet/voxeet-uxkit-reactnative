//
//  RNVoxeetConferencekit.m
//  RNVoxeetConferencekit
//
//  Created by Voxeet on 27/06/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "RNVoxeetConferencekit.h"
//#import "VoxeetRNSample-Swift.h"

@implementation RNVoxeetConferencekit

RCT_EXPORT_MODULE();


RCT_EXPORT_METHOD(initialize:(NSString*)consumerKey
                  consumerSecret:(NSString*)consumerSecret)
{
    [VoxeetModule initialize:consumerKey consumerSecret:consumerSecret];
}

RCT_EXPORT_METHOD(connect:(NSString*)externalId
                  name:(NSString*)name
                  avatarURL:(NSString*)avatarURL
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    [VoxeetModule connect:externalId name:name avatarURL:avatarURL completion:^{
        resolve(@"");
    } onError:^(NSString *error) {
        reject(@"connect_error", error, nil);
    }];
}

RCT_EXPORT_METHOD(disconnect:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    [VoxeetModule disconnect:^{
        resolve(@"");
    } onError:^(NSString *error) {
        reject(@"disconnect_error", error, nil);
    }];
}

//RCT_EXPORT_METHOD(create:(NSDictionary<NSString *,id>*)parameters
//                  resolve:(RCTPromiseResolveBlock)resolve
//                  ejecter:(RCTPromiseRejectBlock)reject)
//{
//  [VoxeetModule createWithParameters:parameters completion:^(NSDictionary<NSString *,id> *response) {
//    resolve(response);
//  } onError:^(NSString *error) {
//    reject(@"startConference_error", error, nil);
//  }];
//}

RCT_EXPORT_METHOD(startConference:(NSString*)conferenceId
                  participants:(NSArray*)participants
                  invite:(BOOL)invite
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    [VoxeetModule startConferenceWithConferenceId:conferenceId users:participants invite:invite completion:^(NSDictionary<NSString *,id> *json) {
        resolve(json);
    } onError:^(NSString *error) {
        reject(@"startConference_error", error, nil);
    }];
}

RCT_EXPORT_METHOD(stopConference:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    [VoxeetModule stopConferenceWithCompletion:^{
        resolve(@"");
    } onError:^(NSString *error) {
        reject(@"stopConference_error", error, nil);
    }];
}

RCT_EXPORT_METHOD(appearMaximized:(BOOL)enabled)
{
    [VoxeetModule appearMaximized:enabled];
}

RCT_EXPORT_METHOD(defaultBuiltInSpeaker:(BOOL)enabled)
{
    [VoxeetModule defaultBuiltInSpeaker:enabled];
}

RCT_EXPORT_METHOD(defaultVideo:(BOOL)enabled)
{
    [VoxeetModule defaultVideo:enabled];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}



@end
