package com.github.devjn.kotlinmap.ui


import com.github.devjn.kotlinmap.common.Consts

import apple.NSObject
import apple.coregraphics.c.CoreGraphics
import apple.coregraphics.struct.CGPoint
import apple.coregraphics.struct.CGRect
import apple.corelocation.struct.CLLocationCoordinate2D
import apple.foundation.NSArray
import apple.foundation.NSBundle
import apple.foundation.NSCoder
import apple.foundation.NSMethodSignature
import apple.foundation.NSSet
import apple.uikit.UISearchController
import apple.uikit.UIView
import apple.uikit.UIViewController
import apple.uikit.protocol.UISearchBarDelegate
import apple.uikit.protocol.UISearchControllerDelegate
import apple.uikit.protocol.UISearchResultsUpdating

import org.moe.googlemaps.GMSCameraPosition
import org.moe.googlemaps.GMSMapView
import org.moe.googlemaps.GMSMarker
import org.moe.googlemaps.GMSServices
import org.moe.natj.c.ann.FunctionPtr
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.Generated
import org.moe.natj.general.ann.Mapped
import org.moe.natj.general.ann.MappedReturn
import org.moe.natj.general.ann.NInt
import org.moe.natj.general.ann.NUInt
import org.moe.natj.general.ann.Owned
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.general.ann.Runtime
import org.moe.natj.general.ptr.VoidPtr
import org.moe.natj.objc.Class
import org.moe.natj.objc.ObjCRuntime
import org.moe.natj.objc.SEL
import org.moe.natj.objc.ann.ObjCClassBinding
import org.moe.natj.objc.ann.ObjCClassName
import org.moe.natj.objc.ann.Selector
import org.moe.natj.objc.map.ObjCObjectMapper


//@ObjCClassBinding
@Runtime(ObjCRuntime::class)
@ObjCClassName("ViewController")
@RegisterOnStartup
class ViewController
protected constructor(peer: Pointer) : UIViewController(peer), UISearchControllerDelegate, UISearchResultsUpdating, UISearchBarDelegate {

    @Selector("init")
    override external fun init(): ViewController

    val containerView = UIView.alloc().init()
    internal lateinit var mapView: GMSMapView
    internal lateinit var searchController : UISearchController

    override fun viewDidLoad() {
        super.viewDidLoad()

        println("Google Maps SDK Version: " + GMSServices.SDKVersion().toString())
        val key = "AIzaSyAHiYhcQaOVobIsHHwivL9wmSmMWiBtSC4"
        val result = GMSServices.provideAPIKey(key)
        println("provideAPIKey result: " + if (result) "YES" else "NO")

        val camera = GMSCameraPosition.cameraWithLatitudeLongitudeZoom(
                Consts.testLat, Consts.testLng, Consts.defaultZoom) as GMSCameraPosition
        mapView = GMSMapView.mapWithFrameCamera(CoreGraphics.CGRectZero(), camera) as GMSMapView
        mapView.isMyLocationEnabled = true
        mapView.settings().setMyLocationButton(true)
        setView(mapView)

        initViews()
    }

    private fun initViews() {
        val marker = GMSMarker.alloc().init()
        marker.setPosition(CLLocationCoordinate2D(Consts.testLat, Consts.testLng))
        marker.setTitle("Test")
        marker.setSnippet("Test location")
        marker.setMap(mapView)

        initSearchBar()
    }

    private fun initSearchBar() {

        this.searchController = UISearchController.alloc().initWithSearchResultsController(null)

        this.searchController.setSearchResultsUpdater(this)
        this.searchController.setDelegate(this)
        this.searchController.searchBar().setDelegate(this)

        this.searchController.setHidesNavigationBarDuringPresentation(false)
        this.searchController.setDimsBackgroundDuringPresentation(true)

        this.navigationItem().setTitleView(searchController.searchBar())

        this.setDefinesPresentationContext(true)
    }

    override fun updateSearchResultsForSearchController(searchController: UISearchController?) {

    }


    companion object {
        init {
            NatJ.register()
        }

        @Owned
        @Selector("alloc")
        @JvmStatic external fun alloc(): ViewController
    }


}