package com.github.devjn.kotlinmap.ui


import apple.coregraphics.c.CoreGraphics
import apple.corelocation.struct.CLLocationCoordinate2D
import apple.uikit.UISearchController
import apple.uikit.UIView
import apple.uikit.UIViewController
import apple.uikit.protocol.UISearchBarDelegate
import apple.uikit.protocol.UISearchControllerDelegate
import apple.uikit.protocol.UISearchResultsUpdating
import com.github.devjn.kotlinmap.common.Consts
import org.moe.googlemaps.GMSCameraPosition
import org.moe.googlemaps.GMSMapView
import org.moe.googlemaps.GMSMarker
import org.moe.googlemaps.GMSServices
import org.moe.googlemapsiosutils.GMUClusterManager
import org.moe.natj.general.NatJ
import org.moe.natj.general.Pointer
import org.moe.natj.general.ann.Owned
import org.moe.natj.general.ann.RegisterOnStartup
import org.moe.natj.general.ann.Runtime
import org.moe.natj.objc.ObjCRuntime
import org.moe.natj.objc.ann.ObjCClassName
import org.moe.natj.objc.ann.Selector


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
    internal lateinit var searchController: UISearchController

    private lateinit var clusterManager: GMUClusterManager

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

/*    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        setupClaster()
    }

    private fun setupClaster() {
        // Set up the cluster manager with the supplied icon generator and renderer.
        val iconGenerator = GMUDefaultClusterIconGenerator.alloc().init();
        val algorithm = GMUNonHierarchicalDistanceBasedAlgorithm.alloc().init()
        val renderer = GMUDefaultClusterRenderer.alloc().init() // (mapView, iconGenerator);
        clusterManager = GMUClusterManager.alloc().initWithMapAlgorithmRenderer(mapView, algorithm, renderer)

        // Generate and add random items to the cluster manager.
        generateClusterItems()

        // Call cluster() after items have been added to perform the clustering
        // and rendering on map.
        clusterManager.cluster()
    }


    /// Randomly generates cluster items within some extent of the camera and adds them to the cluster manager.
    private fun generateClusterItems() {
        var extent = 0.2
        for (index in 1..200) {
            val lat = Consts.testLat + extent * randomScale()
            val lng = Consts.testLng + extent * randomScale()
            val name = "Item $index"
            val item = POIItem.alloc().init() as POIItem
            item.name = name
            item.position = CLLocationCoordinate2DMake(lat, lng)
            clusterManager.addItem(item)
        }
    }

    /// Returns a random value between -1.0 and 1.0.
    private fun randomScale(): Double {
        return arc4random().toDouble() / Double.MAX_VALUE * 2.0 - 1.0
    }

    /// Point of Interest Item which implements the GMUClusterItem protocol.
    @ObjCClassName("POIItem")
    @RegisterOnStartup
    class POIItem : NSObject, GMUClusterItem {
        @Selector("position")
        override fun position(): CLLocationCoordinate2D? = position

        var position: CLLocationCoordinate2D? = null
        var name: String = "name"

        constructor(peer: Pointer) : super(peer)

        constructor(peer: Pointer, position: CLLocationCoordinate2D, name: String) : super(peer) {
            this.position = position
            this.name = name
        }

        companion object {
            @Owned
            @Selector("alloc")
            @JvmStatic external fun alloc(): POIItem

            @Selector("init")
            @JvmStatic external fun init(): POIItem

            @Selector("initWithNibName:name:")
            @JvmStatic external fun initWithPostitionName(position: CLLocationCoordinate2D, name: String): POIItem
        }

    }*/


    override fun updateSearchResultsForSearchController(searchController: UISearchController?) {
        println("--- updateSearchResultsForSearchController");
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