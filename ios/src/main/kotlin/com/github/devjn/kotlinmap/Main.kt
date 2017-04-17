package com.github.devjn.kotlinmap

import apple.NSObject
import apple.foundation.NSDictionary
import apple.uikit.UIApplication
import apple.uikit.UINavigationController
import apple.uikit.UIScreen
import apple.uikit.UIWindow
import apple.uikit.c.UIKit
import apple.uikit.protocol.UIApplicationDelegate
import com.github.devjn.kotlinmap.ui.MapViewController
import com.github.devjn.kotlinmap.ui.ViewController
import org.moe.googlemaps.GMSServices

import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.objc.ann.Selector

@RegisterOnStartup
class Main protected constructor(peer: Pointer) : NSObject(peer), UIApplicationDelegate {

    private var window: UIWindow? = null

    override fun applicationDidFinishLaunchingWithOptions(application: UIApplication?, launchOptions: NSDictionary<*, *>?): Boolean {
//        println("Google Maps SDK Version: " + GMSServices.SDKVersion().toString())
//        val key = "AIzaSyDBNHlacyZBHNJVbjv90p7vVE0VnflUTIE"
//        val result = GMSServices.provideAPIKey(key)
//        println("provideAPIKey result: " + if (result) "YES" else "NO")

        val vc = ViewController.alloc().init()
        val navigationController = UINavigationController.alloc().init()

        navigationController.initWithRootViewController(vc)

        val screen = UIScreen.mainScreen()

        val bounds = screen.bounds()
        window = UIWindow.alloc().initWithFrame(bounds)

        window!!.setRootViewController(navigationController)

        window!!.makeKeyAndVisible()

        return true
    }

    override fun setWindow(value: UIWindow?) {
        window = value
    }

    override fun window(): UIWindow? {
        return window
    }

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            UIKit.UIApplicationMain(0, null, null, Main::class.java.name)
        }

        @Selector("alloc")
        @JvmStatic external fun alloc(): Main
    }
}
