package com.github.devjn.kotlinmap

import com.github.devjn.kotlinmap.utils.PlacePoint
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by @author Jahongir on ${date}
 *
 * ${file_name}
 */
class PlaceClusterItem : ClusterItem {

    private var mPosition: LatLng
    private var mTitle: String = ""
    private var mSnippet: String = ""
    var mPlace: PlacePoint? = null

    constructor(place : PlacePoint) {
        mPosition = LatLng(place.latitude, place.longitude)
        mTitle = place.name
        mSnippet = place.detailName
        mPlace = place
    }

    constructor(lat: Double, lng: Double)  {
        mPosition = LatLng(lat, lng)
    }

    constructor(lat: Double, lng: Double, title: String, snippet: String, place : PlacePoint) {
        mPosition = LatLng(lat, lng)
        mTitle = title
        mSnippet = snippet
        mPlace = place
    }

    override fun getTitle(): String = mTitle

    override fun getPosition(): LatLng = mPosition

    override fun getSnippet(): String = mSnippet

}