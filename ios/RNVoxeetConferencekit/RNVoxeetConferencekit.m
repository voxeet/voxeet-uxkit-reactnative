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

@interface RNVoxeetConferencekit()

@property (nonatomic, copy) void (^refreshAccessTokenClosure)(NSString *);

@end

@implementation RNVoxeetConferencekit
{
    BOOL _hasListeners;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initialize:(NSString *)consumerKey
                  consumerSecret:(NSString *)consumerSecret
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.pushNotification.type = VTPushNotificationTypeCallKit;
        
        [VoxeetSDK.shared initializeWithConsumerKey:consumerKey consumerSecret:consumerSecret userInfo:nil connectSession:YES];
        [VoxeetConferenceKit.shared initialize];
        resolve(nil);
    });
}

RCT_EXPORT_METHOD(initializeToken:(NSString *)accessToken
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.pushNotification.type = VTPushNotificationTypeCallKit;
        
        [VoxeetSDK.shared initializeWithAccessToken:accessToken userInfo:nil refreshTokenClosure:^(void (^closure)(NSString *)) {
            self.refreshAccessTokenClosure = closure;
            if (self->_hasListeners) {
                [self sendEventWithName:@"refreshToken" body:nil];
            }
        }];
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

RCT_EXPORT_METHOD(create:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    NSMutableDictionary *nativeOptions = [[NSMutableDictionary alloc] init];
    [nativeOptions setValue:[options valueForKey:@"alias"] forKey:@"conferenceAlias"];
    
    NSDictionary *params = [options valueForKey:@"params"];
    if (params) {
        NSMutableDictionary *nativeOptionsParams = [[NSMutableDictionary alloc] init];
        [nativeOptionsParams setValue:[params valueForKey:@"ttl"] forKey:@"ttl"];
        [nativeOptionsParams setValue:[params valueForKey:@"rtcpMode"] forKey:@"rtcpMode"];
        [nativeOptionsParams setValue:[params valueForKey:@"mode"] forKey:@"mode"];
        [nativeOptionsParams setValue:[params valueForKey:@"videoCodec"] forKey:@"videoCodec"];
        [nativeOptions setValue:nativeOptionsParams forKey:@"params"];
        
        if ([params valueForKey:@"liveRecording"]) {
            [nativeOptions setValue:@{@"liveRecording": [params valueForKey:@"liveRecording"]} forKey:@"metadata"];
        }
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[VoxeetSDK.shared conference] createWithParameters:nativeOptions success:^(NSDictionary<NSString *,id> *response) {
            resolve(response);
        } fail:^(NSError *error) {
            reject(@"create_error", [error localizedDescription], nil);
        }];
    });
}

RCT_EXPORT_METHOD(join:(NSString *)conferenceID
                  options:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    NSMutableDictionary *nativeOptions = [[NSMutableDictionary alloc] init];
    [nativeOptions setValue:[options valueForKey:@"alias"] forKey:@"conferenceAlias"];
    
    NSDictionary *user = [options valueForKey:@"user"];
    if (user) {
        [nativeOptions setValue:[user valueForKey:@"type"] forKey:@"participantType"];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        BOOL video = VoxeetSDK.shared.conference.defaultVideo;
        [[VoxeetSDK.shared conference] joinWithConferenceID:conferenceID video:video userInfo:nativeOptions success:^(NSDictionary<NSString *,id> *response) {
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

RCT_EXPORT_METHOD(setAudio3DEnabled:(BOOL)enable)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.conference.audio3D = enable;
    });
}

RCT_EXPORT_METHOD(setTelecomMode:(BOOL)enable)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetConferenceKit.shared.telecom = enable;
    });
}

RCT_EXPORT_METHOD(isAudio3DEnabled:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([NSNumber numberWithBool:VoxeetSDK.shared.conference.audio3D]);
}

RCT_EXPORT_METHOD(isTelecomMode:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([NSNumber numberWithBool:VoxeetConferenceKit.shared.telecom]);
}

RCT_EXPORT_METHOD(defaultVideo:(BOOL)enable)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [[[VoxeetSDK shared] conference] setDefaultVideo:enable];
    });
}

/*
 *  MARK: Oauth2 helpers
 */

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"refreshToken"];
}

// Will be called when this module's first listener is added.
- (void)startObserving
{
    _hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving
{
    _hasListeners = NO;
}

RCT_EXPORT_METHOD(onAccessTokenOk:(NSString *)accessToken
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    self.refreshAccessTokenClosure(accessToken);
    resolve(accessToken);
}

RCT_EXPORT_METHOD(onAccessTokenKo:(NSString *)error
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    self.refreshAccessTokenClosure(nil);
    resolve(nil);
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
