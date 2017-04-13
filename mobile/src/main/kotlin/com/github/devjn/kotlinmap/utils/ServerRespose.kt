package com.github.devjn.kotlinmap.utils

import org.ferriludium.simplegeoprox.MapObjectHolder

class ServerRespose {

    class MapAll {
        var version: Int = 0
            private set
        var placePoints: Collection<MapObjectHolder<PlacePoint>>

        constructor(version: Int, placePoints: Collection<MapObjectHolder<PlacePoint>>) {
            this.version = version
            this.placePoints = placePoints
        }

    }

}
