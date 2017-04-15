package com.github.devjn.kotlinmap

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.NavigationView
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.SearchView
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.github.devjn.kotlinmap.Common.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.github.devjn.kotlinmap.Common.Companion.STORAGE_PERMISSION_REQUEST_CODE
import com.github.devjn.kotlinmap.common.PlaceClusterItem
import com.github.devjn.kotlinmap.common.PlacePoint
import com.github.devjn.kotlinmap.databinding.ActivityMainBinding
import com.github.devjn.kotlinmap.utils.PermissionUtils
import com.github.devjn.kotlinmap.utils.UIUtils
import com.github.devjn.kotlinmap.utils.UIUtils.getBitmap
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import org.json.JSONException
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResponseService.LocationResultListener {
    val TAG = MainActivity::class.java.kotlin.simpleName
    private lateinit var mGoogleApiClient: GoogleApiClient

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var mPermissionDenied = false

    private lateinit var binding: ActivityMainBinding
    private var mGoogleMap: GoogleMap? = null
    private var mLastLocation: Location? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottomSheet: View

    private lateinit var mResponseService: ResponseService
    private var locationManager: LocationManager? = null
    private var provider: String? = null
    private val testLat = 60.178
    private val testLng = 24.928

    private val mMarkersMap = HashMap<Marker, PlaceClusterItem>(3)
    private lateinit var mClusterManager: ClusterManager<PlaceClusterItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)

        bottomSheet = binding.appBarMain.bottomSheet.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if(slideOffset > 0.5f)
                    binding.appBarMain.fab.hide()
                else binding.appBarMain.fab.show()
            }
        })

        val drawer = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build()

        val fab = binding.appBarMain.fab
        fab.setOnClickListener { v ->
            onPickButtonClick()
            if (mGoogleApiClient.isConnected) {
                if (ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSIONS_REQUEST_CODE)
                } else {
                    //callPlaceDetectionApi();
                }
            } else
                Log.e(TAG, "mGoogleApiClient is not connected")
        }
        initLocationServices()
    }

    private fun initLocationServices() {
        this.mResponseService = ResponseService.instance
        mResponseService.setListener(this)

        // Get the location manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Define the criteria how to select the locatioin provider -> use default
        val criteria = Criteria()
        provider = locationManager!!.getBestProvider(criteria, true)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return
        provider?.let { mLastLocation = locationManager!!.getLastKnownLocation(provider!!) }

        // Initialize the location fields
        val lastLocation: Location? = mLastLocation;
        if (lastLocation != null) {
            println("Provider $provider has been selected.")
            onLocationChanged(lastLocation)
        } else {
            Log.w(TAG, "Location not available")
        }
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onStop() {
        mGoogleApiClient.disconnect()
        super.onStop()
    }

    override fun onBackPressed() {
        val drawer = binding.drawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)

        val searchManager = this@MainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {
            val builder = PlacePicker.IntentBuilder()
                    .setLatLngBounds(LatLngBounds.Builder()
                            .include(LatLng(60.1455, 24.9067))
                            .include(LatLng(60.1782, 24.9530))
                            .build())
            try {
                startActivityForResult(builder.build(this@MainActivity), REQUEST_PLACE_PICKER)
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            }

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.action_settings) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    /* Request updates at startup */
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            provider?.let {
                locationManager?.requestLocationUpdates(provider, 4000, 10f, this)
            } ?: println("null provider")
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager?.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        this.mLastLocation = location
        val lat = location.latitude.toInt()
        val lng = location.longitude.toInt()
        Log.i(TAG, lat.toString())
        Log.i(TAG, lng.toString())
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // TODO Auto-generated method stub
    }

    override fun onProviderEnabled(provider: String) {
        println("Enabled new provider: " + provider)
    }

    override fun onProviderDisabled(provider: String) {
        println("Disabled provider " + provider)
        locationManager?.removeUpdates(this)
        this.provider = locationManager?.getBestProvider(Criteria(), true)
        this.provider?.let { locationManager?.requestLocationUpdates(provider, 4000, 10f, this) }
    }

    internal var geoLayer: GeoJsonLayer? = null

    override fun onMapReady(map: GoogleMap) {
        this.mGoogleMap = map
        val pos = LatLng(testLat, testLng)

        enableMyLocation()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f))

//        map.setOnMarkerClickListener { marker ->
//            updateBottomSheetContent(marker)
//            true
//        }
        map.setOnMapClickListener { bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN }

        map.addMarker(MarkerOptions()
                .title("Test")
                .snippet("Test location.")
                .position(pos))

        setUpClusterer()
    }

    private fun setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = ClusterManager<PlaceClusterItem>(this, mGoogleMap)
        mClusterManager.setOnClusterItemClickListener { item ->
            updateBottomSheetContent(item.`object`)
            false
        }

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mGoogleMap!!.setOnCameraIdleListener(mClusterManager)
        mGoogleMap!!.setOnMarkerClickListener(mClusterManager)
    }

    //GeoJSon
    internal fun initGeoJsonLayer() {
        geoLayer = null
        try {
            geoLayer = GeoJsonLayer(mGoogleMap!!, R.raw.export, applicationContext)
            //            geoLayer.getDefaultPointStyle().setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(drawable)));
            val iterator = geoLayer!!.features.iterator()

            val drawable: Drawable = VectorDrawableCompat.create(resources, R.drawable.ic_menu_camera, null)!!
            val pointStyle = geoLayer!!.defaultPointStyle
            pointStyle.icon = BitmapDescriptorFactory.fromBitmap(getBitmap(drawable))

            val drawableFood = VectorDrawableCompat.create(resources, R.drawable.ic_food, null)!!
            val pointStyle2 = GeoJsonPointStyle()
            pointStyle2.icon = BitmapDescriptorFactory.fromBitmap(getBitmap(drawableFood))

            while (iterator.hasNext()) {
                val feature = iterator.next()
                if (feature.getProperty("name") == null)
                    feature.pointStyle = pointStyle
                else
                    feature.pointStyle = pointStyle2
                geoLayer!!.addFeature(feature)
            }
            geoLayer!!.addLayerToMap()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    /**
     * Enables the My Location geoLayer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (PermissionUtils.isLocationGranted) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true)
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap!!.isMyLocationEnabled = true
        }
    }

    private fun updateBottomSheetContent(marker: Marker) {
        val holder = mMarkersMap[marker]
        if (holder == null) {
            Log.w(TAG, "PlacePoint holder is null")
            if (geoLayer != null) {
                val feature = geoLayer!!.getFeature(marker)
                if (feature == null) {
                    Log.w(TAG, "feature is null")
                    return
                }
                if (feature.hasProperty("name")) {
                    val name = feature.getProperty("name")
                    val placePoint = PlacePoint(name, 0.0, 0.0)
                    if (feature.hasProperty("amenity")) {
                        placePoint.detailName = feature.getProperty("amenity")
                    }
                    binding.appBarMain.bottomSheet.place = placePoint
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            return
        }
        binding.appBarMain.bottomSheet.place = holder.clientObject
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun updateBottomSheetContent(place: PlacePoint) {
        if(place.name.isBlank()) {
            Toast.makeText(this@MainActivity, "No name", Toast.LENGTH_SHORT).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        binding.appBarMain.bottomSheet.place = place
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: " + connectionResult.errorCode)
        Toast.makeText(this,
                "Google Places API connection failed with error code:" + connectionResult.errorCode,
                Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                onRequestPermissionsResult(LOCATION_PERMISSION_REQUEST_CODE, permissions, grantResults)
                onRequestPermissionsResult(STORAGE_PERMISSION_REQUEST_CODE, permissions, grantResults)
            }
            LOCATION_PERMISSION_REQUEST_CODE -> if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location geoLayer if the permission has been granted.
                mPermissionDenied = false
                if (locationManager != null) {
                    if (provider == null) {
                        val criteria = Criteria()
                        provider = locationManager!!.getBestProvider(criteria, false)
                    }
                    locationManager!!.requestLocationUpdates(provider, 4000, 10f, this)
                }
                enableMyLocation()
            } else {
                // Display the missing permission error dialog when the fragments resume.
                Log.w(TAG, "Permissions are not granted: " + permissions)
                mPermissionDenied = true
            }
            STORAGE_PERMISSION_REQUEST_CODE -> if (!PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this@MainActivity, 0, Manifest.permission.READ_EXTERNAL_STORAGE, true)
            }
        }
    }

    @Throws(SecurityException::class)
    private fun callPlaceDetectionApi() {
        Log.d(TAG, "callPlaceDetectionApi")
        val result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null)
        result.setResultCallback { likelyPlaces ->
            for (placeLikelihood in likelyPlaces) {
                Log.i(TAG, String.format("Place '%s' with " + "likelihood: %g",
                        placeLikelihood.place.name,
                        placeLikelihood.likelihood))
            }
            likelyPlaces.release()
        }
    }

    fun onPickButtonClick() {
        Log.d(TAG, "onPickButtonClick")
        onSearchClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PLACE_PICKER && resultCode == Activity.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            val place = PlacePicker.getPlace(this, data)

            val id = place.id
            val name = place.name
            val address = place.address
            val latLng = place.latLng
            var attributions: String? = place.attributions.toString()
            if (attributions == null) {
                attributions = ""
            }
            val intent = Intent()
            intent.putExtra("name", name)
            intent.putExtra("id", id)
            intent.putExtra("lat", latLng.latitude)
            intent.putExtra("lng", latLng.longitude)
            val list = ArrayList(place.placeTypes)
            intent.putIntegerArrayListExtra("types", list)
            startActivity(intent)

            Log.i("Main", "name= " + name + " attributions:\n" + Html.fromHtml(attributions) + "\nList: " + list)
            val toastMsg = String.format("Place: %s", place.name)
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show()

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onLocationResult(result: Collection<PlaceClusterItem>?) {
        Log.i(TAG, "Location result response is received")
        if (mGoogleMap == null || result == null) return
        mClusterManager.addItems(result);
        UIUtils.runOnUIThread(Runnable {
            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mGoogleMap!!.cameraPosition.target, 12f))
            Toast.makeText(applicationContext, R.string.location_updated, Toast.LENGTH_SHORT).show()
        })
    }

    private fun onSearchClick() {
        if (mLastLocation == null) {
            Log.w(TAG, "Last location is null")
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val criteria = Criteria()
                provider = locationManager!!.getBestProvider(criteria, false)
                mLastLocation = locationManager!!.getLastKnownLocation(provider)
                if (mLastLocation == null) {
                    Log.w(TAG, "Location is not available")
                    Toast.makeText(this, R.string.location_not_available, Toast.LENGTH_LONG).show()
                    provider = locationManager!!.getBestProvider(criteria, true)
                    provider?.let { locationManager!!.requestSingleUpdate(provider, this, null) }
//                    return
                }
            } //else return
        }
        var lat: Double = 0.0
        var lng: Double = 0.0
        mLastLocation?.let {
            lat = mLastLocation!!.latitude
            lng = mLastLocation!!.longitude
        } ?: run {
            lat = testLat
            lng = testLng
        }
        Log.i(TAG, "Search click, lat= $lat, lng= $lng")
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(testLat, testLng), 14f))
        showBottomList(lat, lng)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            mPermissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(supportFragmentManager, "dialog")
    }

    internal var listBottomSheet: ListBottomSheetDialogFragment? = null

    private fun showBottomList(lat: Double, lng: Double) {
        if (listBottomSheet == null)
            listBottomSheet = ListBottomSheetDialogFragment.newInstance()
        if (!listBottomSheet!!.isAdded)
            listBottomSheet!!.show(supportFragmentManager, listBottomSheet!!.tag, lat, lng)
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        private val GOOGLE_API_CLIENT_ID = 0
        private val PERMISSIONS_REQUEST_CODE = 100
        private val REQUEST_PLACE_PICKER = 202

    }

}
