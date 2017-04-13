package com.github.devjn.kotlinmap

import com.github.devjn.kotlinmap.utils.PlacePoint
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.ferriludium.simplegeoprox.MapClusterItem

/**
 * Created by @author Jahongir on ${date}
 *
 * ${file_name}
 */
class PlaceClusterItem : MapClusterItem<PlacePoint> {

    private var mPosition: LatLng
    private var mTitle: String = ""
    private var mSnippet: String = ""

    constructor(place : PlacePoint) : super(place) {
        mPosition = LatLng(place.latitude, place.longitude)
        mTitle = place.name
        mSnippet = place.detailName
    }

    constructor(lat: Double, lng: Double, title: String, snippet: String, place : PlacePoint) : super(place) {
        mPosition = LatLng(lat, lng)
        mTitle = title
        mSnippet = snippet
    }

    override fun getTitle(): String = mTitle

    override fun getPosition(): LatLng = mPosition

    override fun getSnippet(): String = mSnippet

}