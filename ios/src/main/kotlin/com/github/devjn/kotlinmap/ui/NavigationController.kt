package com.github.devjn.kotlinmap.ui

import apple.uikit.UIColor
import apple.uikit.UIImage
import apple.uikit.UINavigationController
import apple.uikit.enums.UIBarStyle
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.Owned
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.objc.ObjCRuntime
import org.moe.natj.objc.ann.ObjCClassName
import org.moe.natj.objc.ann.Selector

@org.moe.natj.general.ann.Runtime(ObjCRuntime::class)
@ObjCClassName("NavigationController")
@RegisterOnStartup
class NavigationController protected constructor(peer: Pointer) : UINavigationController(peer) {

    @Selector("init")
    override external fun init(): NavigationController

    @Selector("viewDidLoad")
    override fun viewDidLoad() {
        // Colours
        val colorMain = UIColor.alloc().initWithRedGreenBlueAlpha(0.0, (113 / 255f).toDouble(), (197 / 255f).toDouble(), 1.0)
        val colorDark = UIColor.alloc().initWithRedGreenBlueAlpha(0.0, (60 / 255f).toDouble(), (113 / 255f).toDouble(), 1.0)
        val colorAccent = UIColor.alloc().initWithRedGreenBlueAlpha((255 / 255f).toDouble(), (163 / 255f).toDouble(), 0.0, 1.0)

        navigationBar().setBarStyle(UIBarStyle.Black)
        navigationBar().setBarTintColor(colorMain)
        navigationBar().setShadowImage(UIImage.alloc().init())
        navigationBar().setTintColor(UIColor.whiteColor())
        navigationBar().isTranslucent = false

        toolbar().setTintColor(colorAccent)
    }

    companion object {

        @Owned
        @Selector("alloc")
        @JvmStatic external fun alloc(): NavigationController
    }
}
