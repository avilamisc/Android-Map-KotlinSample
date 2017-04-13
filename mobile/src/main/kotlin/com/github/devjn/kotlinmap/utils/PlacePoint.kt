package com.github.devjn.kotlinmap.utils

class PlacePoint {

    var name: String = ""
    var detailName: String = ""

    var description: String? = null
    var image: String? = null

    var rating: Float = 0f
    var type: Int = 0


    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor() {}

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String?, latitude: Double, longitude: Double) {
        name?.let {this.name = name}
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String?, detail: String, type: Int, latitude: Double, longitude: Double) {
        name?.let {this.name = name}
        this.type = type
        this.detailName = detail
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun toString(): String {
        val s = name + ", lat: " + latitude + ", lng: " + longitude + super.toString()
        return s
    }
}
