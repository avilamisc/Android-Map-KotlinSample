package com.devjn.kotlinmap.utils

class PlacePoint {

    var name: String = ""
    var detailName: String = ""

    var description: String? = null
    var image: String? = null

    var rating: Float = 0.toFloat()
    var type: Int = 0


    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()

    constructor() {

    }

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String, latitude: Double, longitude: Double) {
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String, detail: String, type: Int, latitude: Double, longitude: Double) {
        this.type = type
        this.name = name
        this.detailName = detail
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun toString(): String {
        val s = name + ", lat: " + latitude + ", lng: " + longitude + super.toString()
        return s
    }
}
