package com.github.devjn.kotlinmap.common.services

import com.github.devjn.kotlinmap.common.Consts
import com.github.devjn.kotlinmap.common.GeoJsonConverter
import com.github.devjn.kotlinmap.common.PlaceClusterItem
import com.github.devjn.kotlinmap.common.PlacePoint
import com.github.devjn.kotlinmap.common.services.LocationService.Companion.retrofit
import com.github.devjn.kotlinmap.common.utils.CommonUtils
import com.github.devjn.kotlinmap.common.utils.Log
import com.github.devjn.kotlinmap.common.utils.NativeUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.javadocmd.simplelatlng.util.LengthUnit
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.ferriludium.simplegeoprox.FeSimpleGeoProx
import org.pmw.tinylog.Logger
import java.io.File
import java.util.*


/**
* Created by @author Jahongir on 06-Nov-16
* devjn@jn-arts.com
* ResponseService.kt
*/

class ResponseService {

    interface LocationResultListener {
        fun onLocationResult(result: Collection<PlaceClusterItem>?)
    }

    private var world: FeSimpleGeoProx<PlacePoint>
    private var mapObjects: List<PlaceClusterItem>

    private var mPlacesVersion: Int = 0
    private var locationService: LocationService
    private var mListener: LocationResultListener? = null

    private constructor() {
        mapObjects = ArrayList<PlaceClusterItem>(200)
        world = FeSimpleGeoProx(mapObjects)
        mPlacesVersion = NativeUtils.resolver.placesVersion
        locationService = retrofit.create(LocationService::class.java)
        checkMapObjects()
    }

    fun setListener(listener: LocationResultListener) {
        this.mListener = listener
    }

    private fun checkMapObjects() {
        val call = locationService.version
        call.subscribeOn(Schedulers.io())
                .observeOn(NativeUtils.resolver.mainThread())
                .subscribe({ version ->
                    Log.i(TAG, "versionCallback response: " + version!!)
                    if (version > mPlacesVersion)
                        requestNewVersion()
                    else checkLocal()
                }, { t ->
                    Log.e(TAG, "versionCallback response failed: " + t)
                    checkLocal()
                })
    }

    private fun requestNewVersion() {
        val call = locationService.all
        call.subscribeOn(Schedulers.io())
                .observeOn(NativeUtils.resolver.mainThread())
                .subscribe({ mapAll ->
                    val result = mapAll.placePoints
                    if (result is List<*>) {
                        mapObjects = result as List<PlaceClusterItem>
                        world = FeSimpleGeoProx(mapObjects)
                        Logger.info(TAG, "getAllCallback response: " + result)
                        val gson = GsonBuilder().create()
                        val file = gson.toJson(mapObjects)
                        CommonUtils.writeAsync(Consts.MAP_FILENAME, file)
                        NativeUtils.resolver.placesVersion = mapAll.version
                        mPlacesVersion = mapAll.version
                    }
                }, { t -> Log.e(TAG, "requestNewVersion response failed: " + t) })
    }

    private fun checkLocal() {
        val file = File(NativeUtils.resolver.mapFilePath)
        getPlacesObservable(file.exists()).subscribeOn(Schedulers.io())
                .filter { File(NativeUtils.resolver.mapFilePath).exists() }
                .observeOn(NativeUtils.resolver.mainThread())
                .subscribe({ list ->
                    mapObjects = list
                    world = FeSimpleGeoProx(mapObjects)
                    mListener?.onLocationResult(mapObjects)
                    Log.i(TAG, "world created, size= " + mapObjects.size)
                })
    }

    @Suppress("UNCHECKED_CAST")
    fun getNearLocations(lat: Double, lng: Double) {
        if (mapObjects.isNotEmpty()) {
            val result = world.find(LatLng(lat, lng), 1.0, LengthUnit.KILOMETER)
            mListener!!.onLocationResult(result as Collection<PlaceClusterItem>?)
        } else {
            val call = locationService.nearLocations(lat, lng)
            call.subscribeOn(Schedulers.io())
                    .observeOn(NativeUtils.resolver.mainThread())
                    .subscribe({ result -> mListener!!.onLocationResult(result) }, {})
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getNearLocations(lat: Double, lng: Double, listener: LocationResultListener) {
        if (mapObjects.isNotEmpty()) {
            val result = world.find(LatLng(lat, lng), 1.0, LengthUnit.KILOMETER)
            listener.onLocationResult(result as Collection<PlaceClusterItem>?)
        } else {
            val call = locationService.nearLocations(lat, lng)
            call.subscribeOn(Schedulers.io())
                    .observeOn(NativeUtils.resolver.mainThread())
                    .subscribe({ result -> listener.onLocationResult(result) }, {})
        }
    }

    val allLocations: List<PlaceClusterItem>
        get() = mapObjects

    private object Holder { val INSTANCE = ResponseService() }

    companion object {
        private val TAG = ResponseService::class.java.simpleName

        val instance: ResponseService by lazy { Holder.INSTANCE }

        fun getPlacesObservable (exist: Boolean): Observable<List<PlaceClusterItem>> = Observable.fromCallable  {
            if(exist) {
                val gson = Gson()
                val jsonFile = CommonUtils.read(Consts.MAP_FILENAME)
                val listType = object : TypeToken<List<PlaceClusterItem>>() {}.type
                gson.fromJson<List<PlaceClusterItem>>(jsonFile, listType)
            } else GeoJsonConverter.ConvertLocalJson()
        }
    }

}
