//
//  AppDelegate.swift
//  jazz
//
//  Created by Jared Rewerts & Jackson on 11/28/17 & 06/03/2019.
//  Copyright © 2017 City of Edmonton. All rights reserved.
//

import UIKit
import LiveChat
import GoogleSignIn

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, LiveChatDelegate, GIDSignInDelegate {
    
    var window: UIWindow?
    
    /**
     Called when the app successfully finishes launching.
     
     - Parameter application: Your singleton app object.
     - Parameter launchOptions: A dictionary indicating the reason the app was launched (if any). The contents of this dictionary may be empty in situations where the user launched the app directly.
     
     - Returns: false if the app cannot handle the URL resource or continue a user activity, otherwise return true. The return value is ignored if the app is launched as a result of a remote notification.
     */
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        LiveChat.licenseId = Config.license
        LiveChat.groupId = Config.group
        LiveChat.name = "Unknown iOS User"
        LiveChat.email = "example@livechatinc.com"
        
        // Setting some custom variables:
        LiveChat.setVariable(withKey:"Device", value: UIDevice.current.model)
        LiveChat.setVariable(withKey:"OS", value: UIDevice.current.systemName + " " + UIDevice.current.systemVersion)

        
        LiveChat.delegate = self
        
        GIDSignIn.sharedInstance().clientID = Config.clientID
        GIDSignIn.sharedInstance().delegate = self
        
        return true
    }
    
    /**
     Handles opening URLs for all old versions of iOS (4.2-9.0).
     
     - Parameter app: Your singleton app object.
     - Parameter url: The URL resource to open. This resource can be a network resource or a file.
     - Parameter sourceApplication: The bundle ID of the app that is requesting your app to open the URL (url).
     - Parameter annotation: The property list supploed by the source app.
     
     - Returns: true if the delegate successfully handled the request or false if the attempt to open the URL resource failed.
     */
    func application(_ application: UIApplication,
                     open url: URL,
                     sourceApplication: String?,
                     annotation: Any) -> Bool {
        return GIDSignIn.sharedInstance().handle(url,
                                                 sourceApplication: sourceApplication,
                                                 annotation: annotation)
    }
    
    /**
     Handles opening URLs for all new versions of iOS (>9.0).
     
     - Parameter app: Your singleton app object.
     - Parameter url: The URL resource to open. This resource can be a network resource or a file.
     - Parameter options: A dictionary of URL handling options.
     
     - Returns: true if the delegate successfully handled the request or false if the attempt to open the URL resource failed.
     */
    @available(iOS 9.0, *)
    func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any]) -> Bool {
        return GIDSignIn.sharedInstance().handle(url,
                                                 sourceApplication: options[UIApplicationOpenURLOptionsKey.sourceApplication] as? String,
                                                 annotation: options[UIApplicationOpenURLOptionsKey.annotation])
    }
    
    /**
     Called on sign in. Signs the authenticated user into LiveChat.
     
     - Parameter signIn: The GIDSignIn object which allows for signing users in.
     - Parameter user: The authenticated user.
     - Parameter error: The error object.
     */
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!,
              withError error: Error!) {
        if (error == nil) {
            LiveChat.setVariable(withKey:"token", value:user.authentication.idToken)
            LiveChat.name = user.profile.name
            LiveChat.email = user.profile.email
            LiveChat.presentChat()
        } else {
            print("\(error.localizedDescription)")
        }
    }
    
    // [START disconnect_handler]
    func sign(_ signIn: GIDSignIn!, didDisconnectWith user: GIDGoogleUser!,
              withError error: Error!) {
        print("User disconnected")
    }
    // [END disconnect_handler]
}
