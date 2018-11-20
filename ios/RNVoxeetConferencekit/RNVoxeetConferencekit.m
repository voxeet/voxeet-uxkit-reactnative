//
//  RNVoxeetConferencekit.m
//  RNVoxeetConferencekit
//
//  Created by Corentin Larroque on 11/16/18.
//  Copyright © 2018 Voxeet. All rights reserved.
//

#import "RNVoxeetConferencekit.h"

@import VoxeetSDK;
@import VoxeetConferenceKit;

@implementation RNVoxeetConferencekit
    
    RCT_EXPORT_MODULE();
    
    RCT_EXPORT_METHOD(initialize:(NSString *)consumerKey
                      consumerSecret:(NSString *)consumerSecret
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [VoxeetSDK.shared initializeWithConsumerKey:consumerKey consumerSecret:consumerSecret userInfo:nil connectSession:YES];
            [VoxeetSDK.shared setCallKit:YES];
            [VoxeetConferenceKit.shared initialize];
            resolve(nil);
        });
    }
    
    RCT_EXPORT_METHOD(connect:(NSDictionary *)userInfo
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSString *externalID = [userInfo objectForKey:@"externalId"];
            NSString *name = [userInfo objectForKey:@"name"];
            NSString *avatarURL = [userInfo objectForKey:@"avatarUrl"];
            
            VTUser *user = [[VTUser alloc] initWithExternalID:externalID name:name avatarURL:avatarURL];
            
            [[VoxeetSDK.shared session] connectWithUser:user completion:^(NSError *error) {
                if (error != nil) {
                    reject(@"connect_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    RCT_EXPORT_METHOD(disconnect:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetSDK.shared session] disconnectWithCompletion:^(NSError *error) {
                if (error != nil) {
                    reject(@"disconnect_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    RCT_EXPORT_METHOD(create:(NSDictionary *)parameters
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetSDK.shared conference] createWithParameters:parameters success:^(NSDictionary<NSString *,id> *response) {
                resolve(response);
            } fail:^(NSError *error) {
                reject(@"create_error", [error localizedDescription], nil);
            }];
        });
    }
    
    RCT_EXPORT_METHOD(join:(NSString *)conferenceID
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            BOOL video = VoxeetSDK.shared.conference.defaultVideo;
            [[VoxeetSDK.shared conference] joinWithConferenceID:conferenceID video:video userInfo:nil success:^(NSDictionary<NSString *,id> *response) {
                resolve(response);
            } fail:^(NSError *error) {
                reject(@"join_error", [error localizedDescription], nil);
            }];
        });
    }
    
    RCT_EXPORT_METHOD(leave:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetSDK.shared conference] leaveWithCompletion:^(NSError *error) {
                if (error != nil) {
                    reject(@"leave_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    RCT_EXPORT_METHOD(invite:(NSString *)conferenceID
                      participants:(NSArray *)participants
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSMutableArray *userIDs = [[NSMutableArray alloc] init];
            
            for (NSDictionary *participant in participants) {
                [userIDs addObject:[participant objectForKey:@"externalId"]];
            }
            
            [[VoxeetSDK.shared conference] inviteWithConferenceID:conferenceID externalIDs:userIDs completion:^(NSError *error) {
                if (error != nil) {
                    reject(@"invite_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    RCT_EXPORT_METHOD(sendBroadcastMessage:(NSString *)message
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetSDK.shared conference] broadcastWithMessage:message completion:^(NSError *error) {
                if (error != nil) {
                    reject(@"sendBroadcastMessage_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    RCT_EXPORT_METHOD(appearMaximized:(BOOL)enable)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetConferenceKit shared] setAppearMaximized:enable];
        });
    }
    
    RCT_EXPORT_METHOD(defaultBuiltInSpeaker:(BOOL)enable)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[[VoxeetSDK shared] conference] setDefaultBuiltInSpeaker:enable];
        });
    }
    
    RCT_EXPORT_METHOD(defaultVideo:(BOOL)enable)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[[VoxeetSDK shared] conference] setDefaultVideo:enable];
        });
    }
    
    /*
     *  MARK: Android compatibility methods
     */
    
    /* Android compatibility */
    RCT_EXPORT_METHOD(screenAutoLock:(BOOL)activate)
    {
    }
    
    /* Android compatibility */
    RCT_EXPORT_METHOD(isUserLoggedIn:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            BOOL isLogIn = (VoxeetSDK.shared.session.state == VTSessionStateConnected);
            resolve([NSNumber numberWithBool:isLogIn]);
        });
    }
    
    /* Android compatibility */
    RCT_EXPORT_METHOD(checkForAwaitingConference:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        resolve(nil);
    }
    
    /*
     *  MARK: Deprecated methods
     */
    
    /* Deprecated */
    RCT_EXPORT_METHOD(startConference:(NSString *)conferenceID
                      participants:(NSArray *)participants
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSMutableArray *userIDs = [[NSMutableArray alloc] init];
            
            for (NSDictionary *participant in participants) {
                [userIDs addObject:[participant objectForKey:@"externalId"]];
            }
            
            [[VoxeetSDK.shared conference] createWithParameters:@{@"conferenceAlias": conferenceID} success:^(NSDictionary<NSString *,id> *response) {
                NSString *confID = response[@"conferenceId"];
                BOOL isNew = response[@"isNew"];
                BOOL video = VoxeetSDK.shared.conference.defaultVideo;
                
                [[VoxeetSDK.shared conference] joinWithConferenceID:confID video:video userInfo:nil success:^(NSDictionary<NSString *,id> *response) {
                    resolve(response);
                } fail:^(NSError *error) {
                    reject(@"startConference_error", [error localizedDescription], nil);
                }];
                
                if (isNew) {
                    [[VoxeetSDK.shared conference] inviteWithConferenceID:confID externalIDs:userIDs completion:^(NSError *error) {}];
                }
            } fail:^(NSError *error) {
                reject(@"startConference_error", [error localizedDescription], nil);
            }];
        });
    }
    
    /* Deprecated */
    RCT_EXPORT_METHOD(stopConference:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetSDK.shared conference] leaveWithCompletion:^(NSError *error) {
                if (error != nil) {
                    reject(@"stopConference_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    /* Deprecated */
    RCT_EXPORT_METHOD(openSession:(NSDictionary *)userInfo
                      resolve:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSString *externalID = [userInfo objectForKey:@"externalId"];
            NSString *name = [userInfo objectForKey:@"name"];
            NSString *avatarURL = [userInfo objectForKey:@"avatarUrl"];
            
            VTUser *user = [[VTUser alloc] initWithExternalID:externalID name:name avatarURL:avatarURL];
            
            [[VoxeetSDK.shared session] connectWithUser:user completion:^(NSError *error) {
                if (error != nil) {
                    reject(@"connect_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
    /* Deprecated */
    RCT_EXPORT_METHOD(closeSession:(RCTPromiseResolveBlock)resolve
                      ejecter:(RCTPromiseRejectBlock)reject)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[VoxeetSDK.shared session] disconnectWithCompletion:^(NSError *error) {
                if (error != nil) {
                    reject(@"connect_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        });
    }
    
@end
