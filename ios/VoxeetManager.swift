//
//  VoxeetModule.swift
//  VoxeetRNSample
//
//  Created by Voxeet on 25/06/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

import UIKit
import VoxeetSDK
import VoxeetConferenceKit

@objc class VoxeetModule: NSObject {
    class func application(_ application: UIApplication, didReceive notification: UILocalNotification) {
        VoxeetSDK.shared.application(application, didReceive: notification)
    }
    
    class func application(_ application: UIApplication, handleActionWithIdentifier identifier: String?, for notification: UILocalNotification, completionHandler: @escaping () -> Void) {
        VoxeetSDK.shared.application(application, handleActionWithIdentifier: identifier, for: notification, completionHandler: completionHandler)
    }
    
    class func initialize(_ consumerKey: String, consumerSecret: String) {
        VoxeetSDK.shared.initialize(consumerKey: consumerKey, consumerSecret: consumerSecret)
        VoxeetSDK.shared.callKit = true
        VoxeetConferenceKit.shared.initialize()
    }
    
    class func connect(_ externalId: String, name: String, avatarURL: String, completion: @escaping () -> Void,onError: @escaping (_ error: String) -> Void) {
        let vtUser = VTUser(externalID: externalId, name: name, avatarURL: avatarURL)
        
        VoxeetSDK.shared.session.connect(user: vtUser) { error in
            if let error = error {
                onError(error.localizedDescription)
            } else {
                completion()
            }
        }
    }
    
    class func disconnect(_ completion: @escaping () -> Void, onError: @escaping (_ error: String) -> Void) {
        VoxeetSDK.shared.session.disconnect { error in
            if let error = error {
                onError(error.localizedDescription)
            } else {
                completion()
            }
        }
    }
    
    //  class func create(parameters: [String: Any]? = [String: Any](), completion: @escaping (_ json: [String: Any]?) -> Void, onError: @escaping (_ error: String) -> Void) {
    //    VoxeetSDK.shared.conference.create(parameters: parameters, success: { response in
    //      completion(response)
    //    }, fail: { error in
    //      onError(error.localizedDescription)
    //    })
    //  }
    //
    //  class func join(_ completion: @escaping () -> Void, onError: @escaping (_ error: String) -> Void) {
    //}
    //
    //  class func invite(_ completion: @escaping () -> Void, onError: @escaping (_ error: String) -> Void) {
    //}
    
    class func appearMaximized(_ enabled: Bool) {
        VoxeetConferenceKit.shared.appearMaximized = enabled
    }
    
    class func defaultBuiltInSpeaker(_ enabled: Bool) {
        VoxeetSDK.shared.conference.defaultBuiltInSpeaker = enabled
    }
    
    class func defaultVideo(_ enabled: Bool) {
        VoxeetSDK.shared.conference.defaultVideo = enabled
    }
    
    //  class func openSession(_ participantId: String, participantName: String, avatarURL: String, completion: @escaping () -> Void,onError: @escaping (_ error: String) -> Void) {
    //    let vtUser = VTUser(externalID: participantId, name: participantName, avatarURL: avatarURL)
    //
    //    VoxeetSDK.shared.session.connect(user: vtUser) { error in
    //      if let error = error {
    //        onError(error.localizedDescription)
    //      } else {
    //        completion()
    //      }
    //    }
    //  }
    //
    //  class func closeSession(completion: @escaping () -> Void, onError: @escaping (_ error: String) -> Void) {
    //    VoxeetSDK.shared.session.disconnect { error in
    //      if let error = error {
    //        onError(error.localizedDescription)
    //      } else {
    //        completion()
    //      }
    //    }
    //  }
    
    class func startConference(conferenceId: String, users: [AnyObject], invite: Bool, completion: @escaping (_ json: [String: Any]?) -> Void, onError: @escaping (_ error: String) -> Void) {
        var vtUsers = [VTUser]()
        
        for user in users {
            if let participantId = user["id"] as? String, let participantName = user["name"] as? String {
                let participantAvatar = user["avatar"] as? String
                vtUsers.append(VTUser(externalID: participantId, name: participantName, avatarURL: participantAvatar ?? ""))
            }
        }
        
        // Create conference.
        VoxeetSDK.shared.conference.create(parameters: ["conferenceAlias": conferenceId], success: { response in
            guard let confID = response?["conferenceId"] as? String else { return }
            
            // Join the created conference.
            VoxeetSDK.shared.conference.join(conferenceID: confID, userInfo: nil, success: { json in
                completion(json)
            }, fail: { error in
                onError(error.localizedDescription)
            })
            
            VoxeetSDK.shared.conference.invite(conferenceID: confID, externalIDs: vtUsers.map({ $0.externalID ?? "" }), completion: { error in
            })
        }, fail: { error in
            onError(error.localizedDescription)
        })
    }
    
    class func stopConference(completion: @escaping () -> Void, onError: @escaping (_ error: String) -> Void) {
        VoxeetSDK.shared.conference.leave { error in
            if let error = error {
                onError(error.localizedDescription)
            } else {
                completion()
            }
        }
    }
}
