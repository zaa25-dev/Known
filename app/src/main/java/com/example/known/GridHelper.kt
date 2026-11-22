package com.example.known

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.floor



object GridHelper {
    const val CELL_SIZE = 0.0015

    fun getCellId(lat: Double, lng: Double): String {
        val latIdx = floor(lat / CELL_SIZE).toLong()
        val lngIdx = floor(lng / CELL_SIZE).toLong()
        return "${latIdx}_${lngIdx}"
    }

    fun getCellCorners(cellId: String): List<LatLng> {
        val parts = cellId.split("_")
        val latIdx = parts[0].toLong()
        val lngIdx = parts[1].toLong()

        val south = latIdx * CELL_SIZE
        val north = south + CELL_SIZE
        val west = lngIdx * CELL_SIZE
        val east = west + CELL_SIZE

        return listOf(
            LatLng(south, west), // Bottom-Left
            LatLng(north, west), // Top-Left
            LatLng(north, east), // Top-Right
            LatLng(south, east)  // Bottom-Right
        )
    }

    fun getVisibleCellIds(bounds: LatLngBounds): List<String> {
        val minLatIdx = floor(bounds.southwest.latitude / CELL_SIZE).toLong()
        val maxLatIdx = floor(bounds.northeast.latitude / CELL_SIZE).toLong()
        val minLngIdx = floor(bounds.southwest.longitude / CELL_SIZE).toLong()
        val maxLngIdx = floor(bounds.northeast.longitude / CELL_SIZE).toLong()

        val cellIds = mutableListOf<String>()
        for (latIdx in minLatIdx..maxLatIdx) {
            for (lngIdx in minLngIdx..maxLngIdx) {
                cellIds.add("${latIdx}_${lngIdx}")
            }
        }
        return cellIds
    }
}