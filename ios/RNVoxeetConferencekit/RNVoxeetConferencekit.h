//
//  RNVoxeetConferencekit.h
//  RNVoxeetConferencekit
//
//  Created by Corentin Larroque on 11/16/18.
//  Copyright © 2018 Voxeet. All rights reserved.
//

//#import <Foundation/Foundation.h>

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

@interface RNVoxeetConferencekit : NSObject <RCTBridgeModule>
    
- (void)test;

@end
