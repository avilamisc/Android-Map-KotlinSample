package com.github.devjn.kotlinmap.common.services

import com.github.devjn.kotlinmap.common.PlaceClusterItem


class ServerRespose {

    class MapAll {
        var version: Int = 0
            private set
        var placePoints: Collection<PlaceClusterItem>

        constructor(version: Int, placePoints: Collection<PlaceClusterItem>) {
            this.version = version
            this.placePoints = placePoints
        }

    }

}
