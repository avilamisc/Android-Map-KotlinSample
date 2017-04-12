package com.devjn.kotlinmap

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

class Foreground private constructor() : ActivityLifecycleCallbacks {

    var isForeground: Boolean = false
        private set

    val isBackground: Boolean
        get() = !isForeground

    override fun onActivityPaused(activity: Activity) {
        isForeground = false
    }

    override fun onActivityResumed(activity: Activity) {
        isForeground = true
    }
    // other ActivityLifecycleCallbacks methods omitted for brevity
    // we don't need them, so they are empty anyway ;)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
    }

    override fun onActivityStarted(activity: Activity) {
        // TODO Auto-generated method stub
    }

    override fun onActivityStopped(activity: Activity) {
        // TODO Auto-generated method stub
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
        // TODO Auto-generated method stub
    }

    override fun onActivityDestroyed(activity: Activity) {
        // TODO Auto-generated method stub
    }

    companion object {

        private var instance: Foreground? = null

        fun init(app: Application) {
            if (instance == null) {
                instance = Foreground()
                app.registerActivityLifecycleCallbacks(instance)
            }
        }

        fun get(): Foreground {
            return instance!!
        }
    }

}
