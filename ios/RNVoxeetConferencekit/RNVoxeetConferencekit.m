//
//  RNVoxeetConferencekit.m
//  RNVoxeetConferencekit
//
//  Created by Kevin Le PErf on 22/03/21.
//  Copyright © 2021 Voxeet. All rights reserved.
//

#import "RNVoxeetConferencekit.h"

@import VoxeetSDK;
@import VoxeetUXKit;

static NSString * const kRNUXKitVersionKey = @"RNUXKitVersion";
static NSString * const kRNUXKitComponentName = @"ios-react-native";

@interface RNVoxeetConferencekit()

@property (nonatomic, copy) void (^refreshAccessTokenClosure)(NSString *);

@end

@implementation RNVoxeetConferencekit
{
    BOOL _hasListeners;
    BOOL _deactivatedOverlay;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initialize:(NSString *)consumerKey
                  consumerSecret:(NSString *)consumerSecret
                  deactivateOverlay:(BOOL) deactivateOverlay
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.notification.push.type = VTNotificationPushTypeCallKit;
        [self registerComponentVersion];
        
        [VoxeetSDK.shared initializeWithConsumerKey:consumerKey consumerSecret:consumerSecret];

        self->_deactivatedOverlay = deactivateOverlay;
        if (!deactivateOverlay) {
            [VoxeetUXKit.shared initialize];
        }

        resolve(nil);
    });
}

RCT_EXPORT_METHOD(initializeToken:(NSString *)accessToken
                  deactivateOverlay:(BOOL) deactivateOverlay
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VoxeetSDK.shared.notification.push.type = VTNotificationPushTypeCallKit;
        [self registerComponentVersion];
        
        [VoxeetSDK.shared initializeWithAccessToken:accessToken refreshTokenClosureWithParam:^(void (^closure)(NSString *), BOOL isExpired) {
            self.refreshAccessTokenClosure = closure;
            if (self->_hasListeners) {
                [self sendEventWithName:@"refreshToken" body:nil];
            }
        }];

        self->_deactivatedOverlay = deactivateOverlay;
        if (!deactivateOverlay) {
            [VoxeetUXKit.shared initialize];
        }
        
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
        
        VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                              initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                              name:![name isEqual:[NSNull null]] ? name : nil
                                              avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
       
        [VoxeetSDK.shared.session openWithInfo:participantInfo completion:^(NSError *error) {
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
        [VoxeetSDK.shared.session closeWithCompletion:^(NSError *error) {
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
    // Retro compatibility with old params dictionary.
    if ([options valueForKey:@"options"]) {
        options = [options valueForKey:@"options"];
    }
    
    // Create conference options.
    VTConferenceOptions *conferenceOptions = [[VTConferenceOptions alloc] init];
    conferenceOptions.alias = [options valueForKey:@"alias"];
    conferenceOptions.pinCode = [options valueForKey:@"pinCode"];
    NSDictionary *params = [options valueForKey:@"params"];
    if (params) {
        conferenceOptions.params.liveRecording = [params valueForKey:@"liveRecording"];
        conferenceOptions.params.rtcpMode = [params valueForKey:@"rtcpMode"];
        conferenceOptions.params.stats = [params valueForKey:@"stats"];
        conferenceOptions.params.ttl = [params valueForKey:@"ttl"];
        conferenceOptions.params.videoCodec = [params valueForKey:@"videoCodec"];
        conferenceOptions.params.dolbyVoice = [params valueForKey:@"dolbyVoice"];
    }
    
    // Create conference.
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithOptions:conferenceOptions success:^(VTConference *conference) {
            resolve([self convertFromConference:conference]);
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
    BOOL isListener = NO;
    BOOL defaultVideo = VoxeetSDK.shared.conference.defaultVideo; /* Monkey patch with listener mode */
    
    // Retro compatibility with old params dictionary.
    if ([options valueForKey:@"options"]) {
        options = [options valueForKey:@"options"];
    }
    
    // Join conference options.
    NSDictionary *user = [options valueForKey:@"user"];
    if (user) {
        NSString *type = [user valueForKey:@"type"];
        if (type && [type isEqual:@"listener"]) {
            isListener = YES;
        }
    }
    
    // Join conference.
    dispatch_async(dispatch_get_main_queue(), ^{
        VTJoinOptions *options = [[VTJoinOptions alloc] init];
        options.constraints.video = VoxeetSDK.shared.conference.defaultVideo;
        [VoxeetSDK.shared.conference fetchWithConferenceID:conferenceID completion:^(VTConference *conference) {
            if (!isListener) {
                [VoxeetSDK.shared.conference joinWithConference:conference options:options success:^(VTConference *conference2) {

                    resolve([self convertFromConference:conference2]);
                } fail:^(NSError *error) {
                    reject(@"join_error", [error localizedDescription], nil);
                }];
            } else {
                VoxeetSDK.shared.conference.defaultVideo = NO;
                [VoxeetSDK.shared.conference listenWithConference:conference options:nil success:^(VTConference *conference2) {
                    VoxeetSDK.shared.conference.defaultVideo = defaultVideo;

                    resolve([self convertFromConference:conference2]);
                } fail:^(NSError *error) {
                    reject(@"join_error", [error localizedDescription], nil);
                }];
            }
        }];
    });
}

RCT_EXPORT_METHOD(leave:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference leaveWithCompletion:^(NSError *error) {
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
        NSMutableArray<VTParticipantInfo *> *participantInfos = [[NSMutableArray alloc] init];
        
        for (NSDictionary *participant in participants) {
            NSString *externalID = [participant objectForKey:@"externalId"];
            NSString *name = [participant objectForKey:@"name"];
            NSString *avatarURL = [participant objectForKey:@"avatarUrl"];
            
            VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                                  initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                                  name:![name isEqual:[NSNull null]] ? name : nil
                                                  avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
            [participantInfos addObject:participantInfo];
        }
        
        [VoxeetSDK.shared.conference fetchWithConferenceID:conferenceID completion:^(VTConference *conference) {
            [VoxeetSDK.shared.notification inviteWithConference:conference participantInfos:participantInfos completion:^(NSError *error) {
                if (error != nil) {
                    reject(@"invite_error", [error localizedDescription], nil);
                } else {
                    resolve(nil);
                }
            }];
        }];
    });
}

RCT_EXPORT_METHOD(startVideo:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference startVideoWithParticipant:nil completion:^(NSError *error) {
            if (error != nil) {
                reject(@"startVideo_error", [error localizedDescription], nil);
            } else {
                resolve([NSNumber numberWithBool:TRUE]);
            }
        }];
    });
}

RCT_EXPORT_METHOD(stopVideo:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference stopVideoWithParticipant:nil completion:^(NSError *error) {
            if (error != nil) {
                reject(@"stopVideo_error", [error localizedDescription], nil);
            } else {
                resolve([NSNumber numberWithBool:TRUE]);
            }
        }];
    });
}

RCT_EXPORT_METHOD(current:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VTConference *conference = VoxeetSDK.shared.conference.current;
        if(conference) {
            resolve([self convertFromConference:conference]);
            return;
        }
        
        resolve(nil);
    });
}


RCT_EXPORT_METHOD(participants:(NSString *)conferenceID
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        VTConference *conference = VoxeetSDK.shared.conference.current;
        if(conference) {
            NSArray<VTParticipant *> *participants = conference.participants;
            NSMutableArray<NSDictionary *> *output = [[NSMutableArray alloc] init];

            for (VTParticipant *participant in participants) {
                NSDictionary *result = [self convertFromParticipant:participant];
                [output addObject:result];
            }
            
            resolve(output);
            return;
        }

        [VoxeetSDK.shared.conference fetchWithConferenceID:conferenceID completion:^(VTConference *conference) {

            NSArray<VTParticipant *> *participants = conference.participants;
            NSMutableArray<NSDictionary *> *output = [[NSMutableArray alloc] init];

            for (VTParticipant *participant in participants) {
                NSDictionary *result = [self convertFromParticipant:participant];
                [output addObject:result];
            }
            
            resolve(output);
        }];
    });
}

RCT_EXPORT_METHOD(streams:(NSString *)participantID
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        
        if (!conference) {
            resolve(nil);
            return;
        }

        NSArray<VTParticipant *> *participants = conference.participants;
        NSMutableArray<NSDictionary *> *output = [[NSMutableArray alloc] init];

        for (VTParticipant *participant in participants) {
            
            if ([participant.id isEqualToString: participantID]) {
                for (MediaStream *stream in participant.streams) {
                    NSDictionary *result = [self convertFromStream:stream];
                    [output addObject:result];
                }
            }
        }
        resolve(output);
    });
}

RCT_EXPORT_METHOD(sendBroadcastMessage:(NSString *)message
                  resolve:(RCTPromiseResolveBlock)resolve
                  ejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.command sendWithMessage:message completion:^(NSError *error) {
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
        if (self->_deactivatedOverlay) return;
        [VoxeetUXKit.shared setAppearMaximized:enable];
    });
}

RCT_EXPORT_METHOD(defaultBuiltInSpeaker:(BOOL)enable)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference setDefaultBuiltInSpeaker:enable];
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
    if (self->_deactivatedOverlay) {
        resolve([NSNumber numberWithBool:FALSE]);
        return;
    }
    resolve([NSNumber numberWithBool:VoxeetUXKit.shared.telecom]);
}

RCT_EXPORT_METHOD(defaultVideo:(BOOL)enable)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference setDefaultVideo:enable];
    });
}

/*
 *  MARK: Help methods
 */
- (void)registerComponentVersion {
    NSString *version = [[[NSBundle bundleForClass:[self class]] infoDictionary] objectForKey:kRNUXKitVersionKey];
    if (version) {
        [VoxeetSDK.shared _registerComponentVersionWithName:kRNUXKitComponentName version:version];
    }
}

/*
 *  MARK: Oauth2 helpers
 */

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"refreshToken", @"ConferenceStatusUpdatedEvent", @"StreamAddedEvent", @"StreamUpdatedEvent", @"StreamRemovedEvent", @"ParticipantAddedEvent", @"ParticipantUpdatedEvent", @"VoxeetConferencekitVideoView"];
}
/*
 *  MARK: Convert helpers
 */
- (NSDictionary *)convertFromParticipant:(VTParticipant *)participant {
    VTConference *conference = VoxeetSDK.shared.conference.current;
    if (conference != nil) {
        NSString *participantStatus = [self convertParticipantStatusFromStatus:participant.status];

        return @{
            @"participantId": participant.id ? participant.id : @"",
            @"conferenceStatus": participantStatus ? participantStatus : @"",
            @"externalId": participant.info.externalID ? participant.info.externalID : [NSNull null],
            @"name": participant.info.name ? participant.info.name : [NSNull null],
            @"avatarUrl": participant.info.avatarURL ? participant.info.avatarURL : [NSNull null],
        };
    }
    return nil;
}

- (NSDictionary *)convertFromStream:(MediaStream *)stream {
    BOOL hasAudioTracks = stream.audioTracks.count > 0;
    BOOL hasVideoTracks = stream.videoTracks.count > 0;
    NSString *streamType = [self convertFromStreamType:stream.type];
    
    return @{
        @"streamId": stream.streamId,
        @"type": streamType,
        @"hasAudioTracks": @(hasAudioTracks),
        @"hasVideoTracks": @(hasVideoTracks)
    };
}

- (NSString *)convertConferenceStatusFromStatus:(VTConferenceStatus)status {
    switch (status) {
        case VTConferenceStatusCreating:
            return @"CREATING";
        case VTConferenceStatusCreated:
            return @"CREATED";
        case VTConferenceStatusJoining:
            return @"JOINING";
        case VTConferenceStatusJoined:
            return @"JOINED";
        case VTConferenceStatusLeaving:
            return @"LEAVING";
        case VTConferenceStatusLeft:
            return @"LEFT";
        case VTConferenceStatusEnded:
            return @"ENDED";
        case VTConferenceStatusDestroyed:
            return @"DESTROYED";
        case VTConferenceStatusError:
            return @"ERROR";
    }
}

- (NSString *)convertParticipantStatusFromStatus:(VTParticipantStatus)status {
    switch (status) {
        case VTParticipantStatusReserved:
            return @"RESERVED";
        case VTParticipantStatusInactive:
            return @"INACTIVE";
        case VTParticipantStatusDecline:
            return @"DECLINE";
        case VTParticipantStatusConnecting:
            return @"CONNECTING";
        case VTParticipantStatusConnected:
            return @"ON_AIR";
        case VTParticipantStatusLeft:
            return @"LEFT";
        case VTParticipantStatusWarning:
            return @"WARNING";
        case VTParticipantStatusError:
            return @"ERROR";
        case VTParticipantStatusKicked:
            return @"KICKED";
    }
}

- (NSString *)convertFromStreamType:(MediaStreamType)type {
    switch(type) {
        case ScreenShare:
            return @"ScreenShare";
        case Custom:
            return @"Custom";
        case Camera:
            return @"Camera";
    }
}

- (NSDictionary *)convertFromConference:(VTConference *)conference {
    NSArray<VTParticipant *> *participants = conference.participants;
    NSMutableArray<NSDictionary *> *output = [[NSMutableArray alloc] init];

    for (VTParticipant *participant in participants) {
        NSDictionary *result = [self convertFromParticipant:participant];
        [output addObject:result];
    }

    NSString *statusStr = [self convertConferenceStatusFromStatus:conference.status];

    return @{
        @"conferenceId": conference.id,
        @"conferenceAlias": conference.alias,
        @"status": statusStr,
        @"isNew": [NSNumber numberWithBool:conference.isNew],
        @"participants": output
    };
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
 *  MARK: Observers
 */
// Will be called when this module's first listener is added.
- (void)startObserving
{
    _hasListeners = YES;
    // Observers.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(conferenceStatusUpdated:) name:@"VTConferenceStatusUpdated" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(streamAdded:) name:@"VTStreamAdded" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(streamUpdated:) name:@"VTStreamUpdated" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(streamRemoved:) name:@"VTStreamRemoved" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(participantAdded:) name:@"VTParticipantAdded" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(participantUpdated:) name:@"VTParticipantUpdated" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(VoxeetConferencekitVideoView:) name:@"VoxeetConferencekitVideoView" object:nil];
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving
{
    _hasListeners = NO;
    // Observers.
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)participantAdded:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTParticipant *participant = notification.userInfo[@"participant"];
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        if (conference != nil) {
            NSDictionary *result = @{
                @"participant": [self convertFromParticipant:participant]
            };
            [self sendEventWithName:@"ParticipantAddedEvent" body:result];
        }
    });
}

- (void)participantUpdated:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTParticipant *participant = notification.userInfo[@"participant"];
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        if (conference != nil) {
            NSDictionary *result = @{
                @"participant": [self convertFromParticipant:participant]
            };
            [self sendEventWithName:@"ParticipantUpdatedEvent" body:result];
        }
    });
}

- (void)conferenceStatusUpdated:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSNumber *rawStatus = notification.userInfo[@"status"];
        VTConferenceStatus status = (VTConferenceStatus)rawStatus.intValue;
        NSString *statusStr = [self convertConferenceStatusFromStatus:status];
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        NSDictionary *statusDict = @{
            @"status": statusStr,
            @"conferenceId": conference ? conference.id : [NSNull null],
            @"conferenceAlias": conference ? conference.alias : [NSNull null]
        };
        [self sendEventWithName:@"ConferenceStatusUpdatedEvent" body:statusDict];
    });
}


- (void)VoxeetConferencekitVideoView:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self sendEventWithName:@"VoxeetConferencekitVideoView" body:notification.userInfo];
    });
}

- (void)streamAdded:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTParticipant *participant = notification.userInfo[@"participant"];
        MediaStream *stream = notification.userInfo[@"stream"];
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        if (conference != nil) {
            NSDictionary *result = @{
                @"participant": [self convertFromParticipant:participant],
                @"mediaStream": [self convertFromStream:stream]
            };
            [self sendEventWithName:@"StreamAddedEvent" body:result];
        }
    });
}

- (void)streamUpdated:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTParticipant *participant = notification.userInfo[@"participant"];
        MediaStream *stream = notification.userInfo[@"stream"];
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        NSLog(@"updating streams %@", notification.userInfo);
        if (conference != nil) {
            NSDictionary *result = @{
                @"participant": [self convertFromParticipant:participant],
                @"mediaStream": [self convertFromStream:stream]
            };
            [self sendEventWithName:@"StreamUpdatedEvent" body:result];
        }
    });
}

- (void)streamRemoved:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        VTParticipant *participant = notification.userInfo[@"participant"];
        MediaStream *stream = notification.userInfo[@"stream"];
        
        VTConference *conference = VoxeetSDK.shared.conference.current;
        if (conference != nil) {
            NSDictionary *result = @{
                @"participant": [self convertFromParticipant:participant],
                @"mediaStream": [self convertFromStream:stream]
            };
            [self sendEventWithName:@"StreamRemovedEvent" body:result];
        }
    });
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
    NSMutableArray<VTParticipantInfo *> *participantInfos = [[NSMutableArray alloc] init];
    
    for (NSDictionary *participant in participants) {
        NSString *externalID = [participant objectForKey:@"externalId"];
        NSString *name = [participant objectForKey:@"name"];
        NSString *avatarURL = [participant objectForKey:@"avatarUrl"];
        
        VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                              initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                              name:![name isEqual:[NSNull null]] ? name : nil
                                              avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
        [participantInfos addObject:participantInfo];
    }
    
    // Create conference options.
    VTConferenceOptions *conferenceOptions = [[VTConferenceOptions alloc] init];
    conferenceOptions.alias = conferenceID;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [VoxeetSDK.shared.conference createWithOptions:conferenceOptions success:^(VTConference *conference) {
            VTJoinOptions *joinOptions = [[VTJoinOptions alloc] init];
            joinOptions.constraints.video = VoxeetSDK.shared.conference.defaultVideo;
            [VoxeetSDK.shared.conference joinWithConference:conference options:joinOptions success:^(VTConference *conference2) {
                resolve(nil);
            } fail:^(NSError *error) {
                reject(@"startConference_error", [error localizedDescription], nil);
            }];
            
            if (conference.isNew) {
                [VoxeetSDK.shared.notification inviteWithConference:conference participantInfos:participantInfos completion:nil];
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
        [VoxeetSDK.shared.conference leaveWithCompletion:^(NSError *error) {
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
        
        VTParticipantInfo *participantInfo = [[VTParticipantInfo alloc]
                                              initWithExternalID:![externalID isEqual:[NSNull null]] ? externalID : nil
                                              name:![name isEqual:[NSNull null]] ? name : nil
                                              avatarURL: ![avatarURL isEqual:[NSNull null]] ? avatarURL : nil];
        
        [VoxeetSDK.shared.session openWithInfo:participantInfo completion:^(NSError *error) {
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
        [VoxeetSDK.shared.session closeWithCompletion:^(NSError *error) {
            if (error != nil) {
                reject(@"connect_error", [error localizedDescription], nil);
            } else {
                resolve(nil);
            }
        }];
    });
}

@end
