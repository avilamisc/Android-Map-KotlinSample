// Copyright (c) 2015, Intel Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
// 3. Neither the name of the copyright holder nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.github.devjn.kotlinmap.ui

import apple.corelocation.CLLocation
import apple.corelocation.CLLocationManager
import apple.corelocation.c.CoreLocation
import apple.corelocation.enums.CLAuthorizationStatus
import apple.corelocation.protocol.CLLocationManagerDelegate
import apple.foundation.NSArray
import apple.foundation.NSError

class LocationManager : CLLocationManagerDelegate {

    enum class State {
        Stopped,
        Running,
        Unavailable,
        Unauthorized
    }

    private var delegate: LocationManagerDelegate? = null
    private var trackingDistance: Float = 0.toFloat()
    private var tracking = false
    private val currentLocation: CLLocation? = null
    var state = State.Stopped
        private set
    private lateinit var locationManager: CLLocationManager

    private fun init() {
        locationManager = CLLocationManager.alloc().init()
        locationManager.setDelegate(this)
        locationManager.setDesiredAccuracy(CoreLocation.kCLLocationAccuracyBestForNavigation())
        locationManager.setPausesLocationUpdatesAutomatically(true)

        tracking = false
        trackingDistance = 30.0f
    }

    // CLLocationManagerDelegate

    override fun locationManagerDidFailWithError(manager: CLLocationManager?, error: NSError?) {
        println("--- Location error: " + error!!)
    }

    override fun locationManagerDidUpdateLocations(manager: CLLocationManager?, locations: NSArray<out CLLocation>?) {
        println("--- locationManagerDidUpdateLocations()")
        val updatedLocation = locations!!.lastObject()

        if (CoreLocation.CLLocationCoordinate2DIsValid(updatedLocation.coordinate())) {
            if (delegate != null) {
                delegate!!.didUpdateLocation(this, updatedLocation)
            }
        }
    }

    fun setDelegate(delegate: LocationManagerDelegate) {
        this.delegate = delegate
    }

    var isTracking: Boolean
        get() = tracking
        set(tracking) {
            this.tracking = tracking
            if (tracking) {
                println("--- Location updated")
                if (delegate != null) {
                    delegate!!.didUpdateTrackingLocation(this, locationManager!!.location())
                }
            }
        }

    val isLocationServicesAvailable: Boolean
        get() {
            if (CLLocationManager.authorizationStatus() == CLAuthorizationStatus.Denied) {
                println("--- Denied")
                return false
            } else if (CLLocationManager.authorizationStatus() == CLAuthorizationStatus.Restricted) {
                println("--- Restricted")
                return false
            }
            return true
        }

    fun currentLocation(): CLLocation {
        return locationManager.location()
    }

    internal fun startUpdatingLocation() {
        if (state == State.Running) {
            return
        }

        locationManager.requestWhenInUseAuthorization()

        locationManager.startUpdatingLocation()
        println("--- startUpdatingLocation")
        state = State.Running
    }

    internal fun stopUpdatingLocation() {
        locationManager.stopUpdatingLocation()
        state = State.Stopped
    }

    companion object {

        val LOCATION_WARNING = "You should enable Location Service in your Simulator: Debug > Location."

        var sharedManager: LocationManager
            private set

        init {
            sharedManager = LocationManager()

            sharedManager.init()

            try {
                Class.forName(CLLocation::class.java.name)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

        }
    }
}
