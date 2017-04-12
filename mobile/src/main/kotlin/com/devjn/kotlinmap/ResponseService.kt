package com.devjn.kotlinmap

import android.content.Context
import android.os.Environment
import android.util.Log
import com.devjn.kotlinmap.LocationService.Companion.retrofit
import com.devjn.kotlinmap.utils.PlacePoint
import com.devjn.kotlinmap.utils.ServerRespose
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.javadocmd.simplelatlng.LatLng
import com.javadocmd.simplelatlng.util.LengthUnit
import org.ferriludium.simplegeoprox.FeSimpleGeoProx
import org.ferriludium.simplegeoprox.MapObjectHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.functions.Action0
import rx.schedulers.Schedulers
import java.io.*

/**
 * Created by Jahongir on 06-Nov-16.
 */

class ResponseService {

    interface LocationResultListener {
        fun onLocationResult(result: Collection<MapObjectHolder<PlacePoint>>?)
    }

    private var mNearVersion: Int = 0
    private var locationService: LocationService? = null
    private var mListener: LocationResultListener? = null

    constructor() {
        mNearVersion = Common.nearVersion
        locationService = retrofit.create(LocationService::class.java)
        checkMapObjects()
    }

    fun setListener(listener: LocationResultListener) {
        this.mListener = listener
    }

    private fun checkMapObjects() {
        val call = locationService!!.version
        call.enqueue(versionCallback)
    }

    private val getAllCallback = object : Callback<ServerRespose.MapAll> {
        override fun onResponse(call: Call<ServerRespose.MapAll>, response: Response<ServerRespose.MapAll>) {
            val mapAll = response.body()
            val result = mapAll.placePoints
            if (result is List<*>) {
                mapObjects = result as List<MapObjectHolder<PlacePoint>>
                world = FeSimpleGeoProx(mapObjects)
                Log.i(TAG, "getAllCallback response: " + result)
                val gson = GsonBuilder().create()
                val file = gson.toJson(mapObjects)
                write(filename, file)
                Common.nearVersion = mapAll.version
                mNearVersion = mapAll.version
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
            if (version > mNearVersion) {
                requestNewVersion()
            } else
                checkLocal()
        }

        override fun onFailure(call: Call<Int>, t: Throwable) {
            Log.e(TAG, "versionCallback response failed: " + t)
            checkLocal()
        }
    }

    private val nearCallback = object : Callback<Collection<MapObjectHolder<PlacePoint>>> {
        override fun onResponse(call: Call<Collection<MapObjectHolder<PlacePoint>>>, response: Response<Collection<MapObjectHolder<PlacePoint>>>) {
            val result = response.body()
            mListener!!.onLocationResult(result)
        }
        override fun onFailure(call: Call<Collection<MapObjectHolder<PlacePoint>>>, t: Throwable) {}
    }

    private fun requestNewVersion() {
        val call = locationService!!.all
        call.enqueue(getAllCallback)
    }

    private fun checkLocal() {
        Schedulers.io().createWorker().schedule(Action0 {
            val file = File(Common.applicationContext.filesDir.path + File.separator + filename)
            if (file.exists()) {
                try {
                    val gson = Gson()
                    val jsonFile = read(filename)
                    val listType = object : TypeToken<List<MapObjectHolder<PlacePoint>>>() {

                    }.type
                    mapObjects = gson.fromJson<List<MapObjectHolder<PlacePoint>>>(jsonFile, listType)
                    world = FeSimpleGeoProx(mapObjects)
                    Log.i(TAG, "world created, size= " + mapObjects!!.size + " content: " + mapObjects)
                    return@Action0
                } catch (e: IOException) {
                    Log.e(TAG, "Read exception: " + e)
                } catch (e: ClassCastException) {
                    Log.wtf(TAG, "mapObjects is not of needed type, exception: " + e)
                }

            }
            requestNewVersion()
        })
    }

    fun getNearLocations(lat: Double, lng: Double) {
        if (world != null) {
            val result = world!!.find(LatLng(lat, lng), 1.0, LengthUnit.KILOMETER)
            mListener!!.onLocationResult(result)
        } else {
            val call = locationService!!.nearLocations(lat, lng)
            call.enqueue(nearCallback)
        }
    }

    fun getNearLocations(lat: Double, lng: Double, listener: LocationResultListener) {
        if (world != null) {
            val result = world!!.find(LatLng(lat, lng), 1.0, LengthUnit.KILOMETER)
            listener.onLocationResult(result)
        } else {
            val call = locationService!!.nearLocations(lat, lng)
            call.enqueue(object : Callback<Collection<MapObjectHolder<PlacePoint>>> {
                override fun onResponse(call: Call<Collection<MapObjectHolder<PlacePoint>>>, response: Response<Collection<MapObjectHolder<PlacePoint>>>) {
                    val result = response.body()
                    listener.onLocationResult(result)
                }

                override fun onFailure(call: Call<Collection<MapObjectHolder<PlacePoint>>>, t: Throwable) {

                }
            })
        }
    }

    val allLocations: List<MapObjectHolder<PlacePoint>>
        get() = mapObjects!!

    private object Holder { val INSTANCE = ResponseService() }



    companion object {

        private val TAG = ResponseService::class.java.simpleName
        private val filename = "mapos"

        private var world: FeSimpleGeoProx<PlacePoint>? = null
        private var mapObjects: List<MapObjectHolder<PlacePoint>>? = null

        val instance: ResponseService by lazy { Holder.INSTANCE }

        //--------Functions---------

        private fun write(filename: String, file: String) {
            Schedulers.io().createWorker().schedule {
                val outputStream: FileOutputStream
                try {
                    outputStream = Common.applicationContext.openFileOutput(filename, Context.MODE_PRIVATE)
                    outputStream.write(file.toByteArray(charset("UTF-8")))
                    outputStream.close()
                    saveChatImage(Common.applicationContext, "file", file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun saveChatImage(context: Context, fileName: String, file: String) {
            Log.i(TAG + "chat", Environment.getExternalStorageDirectory().toString() + " " + Environment.getRootDirectory())
            val directory = File(Environment.getExternalStorageDirectory().toString() + "/Lifetale/")
            if (!directory.exists()) {
                // creates misssing parts of directory
                directory.mkdirs()
            }
            Log.i(TAG + "chat", "name= " + fileName)
            val mypath = File(directory, fileName)
            try {
                val fos = FileOutputStream(mypath)
                fos.write(file.toByteArray(charset("UTF-8")))
                fos.close()
                Log.i(TAG + " save", "path= " + mypath.absolutePath)
            } catch (e: FileNotFoundException) {
                Log.w(TAG + " save", "file not found")
            } catch (e: IOException) {
                Log.w(TAG, "file not saved " + e)
            }

        }

        @Throws(IOException::class)
        private fun read(filename: String): String {
            val fis = Common.applicationContext.openFileInput(filename)
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
