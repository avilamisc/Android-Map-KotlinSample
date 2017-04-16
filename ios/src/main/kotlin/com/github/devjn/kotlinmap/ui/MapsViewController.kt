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

import apple.NSObject
import apple.corelocation.CLLocation
import apple.corelocation.struct.CLLocationCoordinate2D
import apple.uikit.UIImage
import apple.uikit.UISearchBar
import apple.uikit.UIViewController
import apple.uikit.enums.UISearchBarStyle
import apple.uikit.protocol.UISearchBarDelegate
import com.github.devjn.kotlinmap.common.Consts
import org.moe.googlemaps.*
import org.moe.googlemaps.protocol.GMSMapViewDelegate
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.objc.ObjCRuntime
import org.moe.natj.objc.ann.ObjCClassName
import org.moe.natj.objc.ann.Property
import org.moe.natj.objc.ann.Selector

@org.moe.natj.general.ann.Runtime(ObjCRuntime::class)
@ObjCClassName("MapsViewController")
@RegisterOnStartup
class MapsViewController protected constructor(peer: Pointer) : UIViewController(peer), GMSMapViewDelegate, UISearchBarDelegate, LocationManagerDelegate {

    internal interface MapsViewControllerCallback {
        fun onLocationPicked(coordinate: CLLocationCoordinate2D, address: String)
    }

    private var handler: MapsViewControllerCallback? = null

    private var located = false
    private var selectedCoordinate: CLLocationCoordinate2D? = null
    private var address = ""

    @Selector("init")
    override external fun init(): MapsViewController

    private var mapView: GMSMapView? = null
        @Selector("mapView")
        @Property
        external get

    private var searchBar: UISearchBar? = null
        @Selector("searchBar")
        @Property
        external get

    private lateinit var marker: GMSMarker

    override fun viewDidLoad() {
        val camera = GMSCameraPosition.cameraWithTargetZoom(selectedCoordinate, Consts.defaultZoom) as GMSCameraPosition

        mapView!!.setCamera(camera)
        mapView!!.settings().setCompassButton(true)

        // Creates a marker with current location
        marker = GMSMarker.alloc().init()
        marker.setTitle("My location")
        marker.setSnippet("Russia")
        marker.setMap(mapView)

        searchBar!!.setBackgroundImage(UIImage.alloc().init())
        searchBar!!.setSearchBarStyle(UISearchBarStyle.Default)

        searchBar!!.setDelegate(this)
    }

    // UISearchBarDelegate

    override fun searchBarShouldBeginEditing(searchBar: UISearchBar?): Boolean {
        println("--- searchBarShouldBeginEditing")
        return true
    }

    override fun searchBarSearchButtonClicked(searchBar: UISearchBar?) {
        searchBar!!.resignFirstResponder()

        // Do the search...
        println("--- searchBar.text: " + searchBar.text())
    }

    override fun viewWillAppear(animated: Boolean) {
        println("--- viewWillAppear")
        LocationManager.sharedManager.setDelegate(this)
        mapView!!.setDelegate(this)
    }

    @Selector("doneButtonPressed:")
    fun doneButtonPressed(sender: NSObject) {
        handler!!.onLocationPicked(selectedCoordinate!!, address)
        navigationController().popViewControllerAnimated(true)
    }

    @Selector("handleLocationButton:")
    fun handleLocationButton(sender: NSObject) {
        val location = LocationManager.sharedManager.currentLocation()
        if (location != null) {
            val coordinate = location.coordinate()
            val camera = GMSCameraPosition.cameraWithTargetZoom(coordinate, Consts.defaultZoom) as GMSCameraPosition
            mapView!!.animateToCameraPosition(camera)
        } else {
            println(LocationManager.LOCATION_WARNING)
        }
    }

    @Selector("mapView:willMove:")
    override fun mapViewWillMove(mapView: GMSMapView, gesture: Boolean) {
        println("--- willMove")
    }

    @Selector("mapView:idleAtCameraPosition:")
    override fun mapViewIdleAtCameraPosition(mapView: GMSMapView, position: GMSCameraPosition) {
        // In our case position.target equals to projected center of the map
        selectedCoordinate = position.target()
        // TODO: turn on ActivityIndicator
        GMSGeocoder.geocoder().reverseGeocodeCoordinateCompletionHandler(selectedCoordinate) { response, error ->
            if (error != null) {
                handleUnknownAddress()
                return@reverseGeocodeCoordinateCompletionHandler
            }
            // TODO: turn off ActivityIndicator
            if (response != null) {
                handleGeocoderResponse(response.firstResult())
            }
        }
    }

    private fun handleGeocoderResponse(address: GMSAddress) {
        val addressText = address.thoroughfare()

        if (addressText == null || addressText.toLowerCase().contains("unnamed")) {
            handleUnknownAddress()
            return
        }
        this.address = addressText
        searchBar!!.setText(addressText)
    }

    private fun handleUnknownAddress() {
        println("--- handleUnknownAddress")
        address = "Unknown address"
    }

    override fun didUpdateLocation(manager: LocationManager, location: CLLocation) {
        val coordinate = location.coordinate()
        if (!located) {
            located = true
        }
        marker.setPosition(coordinate)
    }

    override fun didUpdateState(manager: LocationManager, state: LocationManager.State) {

    }

    override fun didUpdateTrackingLocation(manager: LocationManager, location: CLLocation) {
        println("--- didUpdateTrackingLocation")
    }

    internal fun pickLocation(coordinate: CLLocationCoordinate2D, handler: MapsViewControllerCallback) {
        selectedCoordinate = coordinate
        this.handler = handler
    }

    companion object {

        @Selector("alloc")
        @JvmStatic external fun alloc(): MapsViewController
    }
}
