package com.github.devjn.kotlinmap.common

import com.github.devjn.kotlinmap.common.PlaceClusterItem
import com.github.devjn.kotlinmap.common.utils.CommonUtils
import com.github.filosganga.geogson.gson.GeometryAdapterFactory
import com.github.filosganga.geogson.model.FeatureCollection
import com.github.filosganga.geogson.model.Geometry
import com.google.gson.GsonBuilder
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * Created by @author Jahongir on ${date}
 *
 * GeoJsonConverter
 */

class GeoJsonConverter {

    private val pointList = ArrayList<PlacePoint>()

    private fun initGeo(mapObjects: MutableList<PlaceClusterItem>) {
        pointList.mapTo(mapObjects, ::PlaceClusterItem)
    }

//    private fun getLatLngForYourGeographicalPointClass(point: PlacePoint): LatLng {
//        return LatLng(point.latitude, point.longitude)
//    }

    private fun readGson(inputStream: InputStream) {
        val gson = GsonBuilder()
                .registerTypeAdapterFactory(GeometryAdapterFactory())
                .create()

        val fileContent = CommonUtils.readTextFile(inputStream)
        val featureCollection = gson.fromJson(fileContent, FeatureCollection::class.java)
        for (feature in featureCollection.features()) {
            if (feature.geometry().type() == Geometry.Type.POINT || feature.geometry() is com.github.filosganga.geogson.model.Point) {
                val point = feature.geometry() as com.github.filosganga.geogson.model.Point
                var name: String? = null
                if (feature.properties().containsKey("name"))
                    name = feature.properties()["name"]!!.asString
                pointList.add(PlacePoint(name, point.lat(), point.lon()))
            }
        }
    }

    companion object {

        fun ConvertLocalJson(inputStream: InputStream, outputStream: FileOutputStream): List<PlaceClusterItem> {
            val mapObjects = ArrayList<PlaceClusterItem>()
            val geoJsonConverter = GeoJsonConverter()
            geoJsonConverter.readGson(inputStream)
            geoJsonConverter.initGeo(mapObjects)
            val gson = GsonBuilder().create()
            val file = gson.toJson(mapObjects)
            CommonUtils.write(Consts.MAP_FILENAME, file, outputStream)
//            Helper.copyFileToDir(Common.MAP_FILENAME, File(Common.applicationContext.filesDir.path + File.separator + Common.MAP_FILENAME))
            return mapObjects
        }
    }

}
