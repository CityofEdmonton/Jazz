//
//  ViewController.swift
//  jazz
//
//  Created by Jared Rewerts & Jackson on 11/28/17 & 06/03/2019.
//  Copyright © 2017 City of Edmonton. All rights reserved.
//

import UIKit
import LiveChat
import MapKit
import GoogleSignIn

class ViewController: UIViewController, GIDSignInUIDelegate {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Jazz"
        GIDSignIn.sharedInstance().uiDelegate = self
        GIDSignIn.sharedInstance().signIn()
    }
    
    @IBAction func openChat(_ sender: Any) {
        //Presenting chat:
        GIDSignIn.sharedInstance().uiDelegate = self
        GIDSignIn.sharedInstance().signIn()
    }

}

