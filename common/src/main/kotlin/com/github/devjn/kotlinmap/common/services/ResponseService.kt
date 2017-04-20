package com.github.devjn.kotlinmap.common.services

import com.github.devjn.kotlinmap.common.Consts
import com.github.devjn.kotlinmap.common.GeoJsonConverter
import com.github.devjn.kotlinmap.common.PlaceClusterItem
import com.github.devjn.kotlinmap.common.PlacePoint
import com.github.devjn.kotlinmap.common.services.LocationService
import com.github.devjn.kotlinmap.common.services.LocationService.Companion.retrofit
import com.github.devjn.kotlinmap.common.utils.CommonUtils
import com.github.devjn.kotlinmap.common.utils.Log
import com.github.devjn.kotlinmap.common.utils.NativeUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.javadocmd.simplelatlng.util.LengthUnit
import org.ferriludium.simplegeoprox.FeSimpleGeoProx
import org.pmw.tinylog.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.functions.Action0
import rx.schedulers.Schedulers
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
* Created by @author Jahongir on 06-Nov-16
* devjn@jn-arts.com
* ResponseService.kt
*/

class ResponseService {

    interface LocationResultListener {
        fun onLocationResult(result: Collection<PlaceClusterItem>?)
    }

    private var mPlacesVersion: Int = 0
    private var locationService: LocationService
    private var mListener: LocationResultListener? = null

    constructor() {
        mPlacesVersion = NativeUtils.resolver.placesVersion
        locationService = retrofit.create(LocationService::class.java)
        checkMapObjects()
    }

    fun setListener(listener: LocationResultListener) {
        this.mListener = listener
    }

    private fun checkMapObjects() {
        val call = locationService.version
        call.enqueue(versionCallback)
    }

    private val getAllCallback = object : Callback<ServerRespose.MapAll> {
        override fun onResponse(call: Call<ServerRespose.MapAll>, response: Response<ServerRespose.MapAll>) {
            val mapAll = response.body()
            val result = mapAll.placePoints
            if (result is List<*>) {
                mapObjects = result as List<PlaceClusterItem>
//                world = FeSimpleGeoProx(mapObjects)
                Logger.info(TAG, "getAllCallback response: " + result)
                val gson = GsonBuilder().create()
                val file = gson.toJson(mapObjects)
                CommonUtils.writeAsync(Consts.MAP_FILENAME, file)
                NativeUtils.resolver.placesVersion = mapAll.version
                mPlacesVersion = mapAll.version
            }
        }

        override fun onFailure(call: Call<ServerRespose.MapAll>, t: Throwable) {
            Log.e(TAG, "getAllCallback response failed: " + t)
        }
    }

    private val versionCallback = object : Callback<Int> {
        override fun onResponse(call: Call<Int>, response: Response<Int>) {
            val version = response.body()
            Log.i(TAG, "versionCallback response: " + version!!)
            if (version > mPlacesVersion) {
                requestNewVersion()
            } else
                checkLocal()
        }

        override fun onFailure(call: Call<Int>, t: Throwable) {
            Log.e(TAG, "versionCallback response failed: " + t)
            checkLocal()
        }
    }

    private val nearCallback = object : Callback<Collection<PlaceClusterItem>> {
        override fun onResponse(call: Call<Collection<PlaceClusterItem>>, response: Response<Collection<PlaceClusterItem>>) {
            val result = response.body()
            mListener!!.onLocationResult(result)
        }
        override fun onFailure(call: Call<Collection<PlaceClusterItem>>, t: Throwable) {}
    }

    private fun requestNewVersion() {
        val call = locationService.all
        call.enqueue(getAllCallback)
    }

    private fun checkLocal() {
        Schedulers.io().createWorker().schedule(Action0 {
            val file = File(NativeUtils.resolver.mapFilePath)
            if (file.exists()) {
                try {
                    val gson = Gson()
                    val jsonFile = read(Consts.MAP_FILENAME)
                    val listType = object : TypeToken<List<PlaceClusterItem>>() {}.type
                    mapObjects = gson.fromJson<List<PlaceClusterItem>>(jsonFile, listType)
                    world = FeSimpleGeoProx(mapObjects)
                    mListener?.onLocationResult(mapObjects)
                    Log.i(TAG, "world created, size= " + mapObjects!!.size + " content: " + mapObjects)
                    return@Action0
                } catch (e: IOException) {
                    Log.e(TAG, "Read exception: " + e)
                } catch (e: ClassCastException) {
                    Log.wtf(TAG, "mapObjects is not of needed type, exception: " + e)
                }
            } else {
                Log.e(TAG, "Map file doesn't exist")
                Schedulers.io().createWorker().schedule({
                    mapObjects = GeoJsonConverter.ConvertLocalJson()
                    world = FeSimpleGeoProx(mapObjects)
                    mListener?.onLocationResult(mapObjects)
                    Log.i(TAG, "world created, size= " + mapObjects!!.size + " content: " + mapObjects)
                })
            }
            requestNewVersion()
        })
    }

    fun getNearLocations(lat: Double, lng: Double) {
        if (world != null) {
            val result = world!!.find(LatLng(lat, lng), 1.0, LengthUnit.KILOMETER)
            mListener!!.onLocationResult(result as Collection<PlaceClusterItem>?)
        } else {
            val call = locationService.nearLocations(lat, lng)
            call.enqueue(nearCallback)
        }
    }

    fun getNearLocations(lat: Double, lng: Double, listener: LocationResultListener) {
        if (world != null) {
            val result = world!!.find(LatLng(lat, lng), 1.0, LengthUnit.KILOMETER)
            listener.onLocationResult(result as Collection<PlaceClusterItem>?)
        } else {
            val call = locationService.nearLocations(lat, lng)
            call.enqueue(object : Callback<Collection<PlaceClusterItem>> {
                override fun onResponse(call: Call<Collection<PlaceClusterItem>>, response: Response<Collection<PlaceClusterItem>>) {
                    val result = response.body()
                    listener.onLocationResult(result)
                }
                override fun onFailure(call: Call<Collection<PlaceClusterItem>>, t: Throwable) {
                }
            })
        }
    }

    val allLocations: List<PlaceClusterItem>
        get() = mapObjects!!

    private object Holder { val INSTANCE = ResponseService() }



    companion object {

        private val TAG = ResponseService::class.java.simpleName

        private var world: FeSimpleGeoProx<PlacePoint>? = null
        protected var mapObjects: List<PlaceClusterItem>? = null

        val instance: ResponseService by lazy { Holder.INSTANCE }

        //--------Functions---------

        @Throws(IOException::class)
        private fun read(filename: String): String {
            val fis = NativeUtils.resolver.openFileInputStreamFor(filename)
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            val sb = StringBuilder()

            bufferedReader.readWhile { it != 1 }.forEach {
                sb.append(it)
            }
            bufferedReader.close()
            isr.close()
            fis.close()
            return sb.toString()
        }
    }

}

inline fun BufferedReader.readWhile(crossinline predicate: (Int) -> Boolean): Sequence<Char> {
    return generateSequence {
        val c = this.read()
        if (c != -1 && predicate(c)) {
            c.toChar()
        } else {
            null
        }
    }
}
