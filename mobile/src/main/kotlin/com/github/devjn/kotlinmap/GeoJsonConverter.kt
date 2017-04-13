package com.github.devjn.kotlinmap

import com.github.devjn.kotlinmap.utils.Helper
import com.github.devjn.kotlinmap.utils.PlacePoint
import com.github.filosganga.geogson.gson.GeometryAdapterFactory
import com.github.filosganga.geogson.model.FeatureCollection
import com.github.filosganga.geogson.model.Geometry
import com.google.gson.GsonBuilder
import com.javadocmd.simplelatlng.LatLng
import java.util.*

/**
 * Created by @author Jahongir on ${date}
 *
 *
 * ${file_name}
 */

class GeoJsonConverter {

    private val pointList = ArrayList<PlacePoint>()

    private fun initGeo(mapObjects: MutableList<PlaceClusterItem>) {
        for (point in pointList) {
//            val loc = getLatLngForYourGeographicalPointClass(point)
            mapObjects.add(PlaceClusterItem( point))
        }
    }

    private fun getLatLngForYourGeographicalPointClass(point: PlacePoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }

    private fun readGson() {
        val gson = GsonBuilder()
                .registerTypeAdapterFactory(GeometryAdapterFactory())
                .create()

        val inputStream = Common.applicationContext.resources.openRawResource(R.raw.export)
        val fileContent = Helper.readTextFile(inputStream)
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

        fun ConvertLocalJson(): List<PlaceClusterItem> {
            val mapObjects = ArrayList<PlaceClusterItem>()
            val geoJsonConverter = GeoJsonConverter()
            geoJsonConverter.readGson()
            geoJsonConverter.initGeo(mapObjects)
            val gson = GsonBuilder().create()
            val file = gson.toJson(mapObjects)
            Helper.write(Common.MAP_FILENAME, file)
//            Helper.copyFileToDir(Common.MAP_FILENAME, File(Common.applicationContext.filesDir.path + File.separator + Common.MAP_FILENAME))
            return mapObjects
        }
    }

}
