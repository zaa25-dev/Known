package com.example.known

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CellData(
    val cellId: String,
    var visitCount: Int,
    var lastMode: String,
    val firstVisitedTimestamp: Long = System.currentTimeMillis()
)

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isFirstLocationFix = true

    private val renderedPolygons = mutableMapOf<String, Polygon>()
    private val visitedCells = mutableMapOf<String, CellData>()
    private var lastCellId: String? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                enableLiveLocation()
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to map your route.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupLocationCallback()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapColorScheme = MapColorScheme.DARK
        checkAndRequestPermissions()

        googleMap.setOnCameraIdleListener {
            renderVisibleGrid()
        }

        // Tap any painted square to see its details
        googleMap.setOnPolygonClickListener { polygon ->
            val cellId = polygon.tag as? String ?: return@setOnPolygonClickListener
            val data = visitedCells[cellId] ?: return@setOnPolygonClickListener

            val dateStr = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.US)
                .format(Date(data.firstVisitedTimestamp))

            // Just temporary, not the actual naming scheme.
            val tierLabel = when {
                data.visitCount == 1 -> "Newly Discovered (Tier 1)"
                data.visitCount in 2..3 -> "Familiar Route (Tier 2)"
                data.visitCount in 4..6 -> "Frequent Territory (Tier 3)"
                else -> "Mastered Zone (Tier 4)"
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Zone $cellId")
                .setMessage(
                    "Status: $tierLabel\n" +
                            "Total Visits: ${data.visitCount}\n" +
                            "Last Mode: ${data.lastMode}\n" +
                            "First Explored: $dateStr"
                )
                .setPositiveButton("Close", null)
                .show()
        }
    }

    private fun renderVisibleGrid() {
        if (!::googleMap.isInitialized) return

        val bounds = googleMap.projection.visibleRegion.latLngBounds


        val iterator = renderedPolygons.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val corners = GridHelper.getCellCorners(entry.key)
            val isOnScreen = corners.any { bounds.contains(it) }
            if (!isOnScreen || entry.key !in visitedCells) {
                entry.value.remove()
                iterator.remove()
            }
        }

        // ONLY render squares that have been visited.
        // This is where the points difference happens
        for ((cellId, cellData) in visitedCells) {
            val corners = GridHelper.getCellCorners(cellId)
            val isOnScreen = corners.any { bounds.contains(it) }
            if (!isOnScreen) continue

            val existingPolygon = renderedPolygons[cellId]
            if (existingPolygon != null) {
                applyCoverageStyle(existingPolygon, cellData.visitCount)
            } else {
                val polygon = googleMap.addPolygon(
                    PolygonOptions()
                        .addAll(corners)
                        .strokeWidth(0f) // Seamless borderless tiles
                        .clickable(true)
                )
                polygon.tag = cellId
                applyCoverageStyle(polygon, cellData.visitCount)
                renderedPolygons[cellId] = polygon
            }
        }
    }

    private fun applyCoverageStyle(polygon: Polygon, visits: Int) {
        polygon.fillColor = when {
            visits == 1 -> 0x55B0BEC5.toInt()     // Soft Silver (1 visit)
            visits in 2..3 -> 0x66CE93D8.toInt()  // Light Purple (2-3 visits)
            visits in 4..6 -> 0x77AB47BC.toInt()  // Medium Purple (4-6 visits)
            else -> 0x886A1B9A.toInt()            // Deep Purple (7+ visits)

            // This is mainly for user insight.
        }
    }

    private fun checkAndRequestPermissions() {
        val hasFine = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine) {
            enableLiveLocation()
        } else {
            val permissionsToRequest = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            locationPermissionRequest.launch(permissionsToRequest.toTypedArray())
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLiveLocation() {
        if (!::googleMap.isInitialized) return

        googleMap.isMyLocationEnabled = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(5f)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val currentLatLng = LatLng(location.latitude, location.longitude)

                if (isFirstLocationFix) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                    isFirstLocationFix = false
                }

                val currentCellId = GridHelper.getCellId(location.latitude, location.longitude)
                if (currentCellId != lastCellId) {
                    lastCellId = currentCellId
                    val speedMph = location.speed * 2.23694f
                    val mode = if (speedMph > 5.0f) "DRIVE" else "WALK"

                    val existing = visitedCells[currentCellId]
                    if (existing != null) {
                        existing.visitCount += 1
                        existing.lastMode = mode
                    } else {
                        visitedCells[currentCellId] = CellData(currentCellId, visitCount = 1, lastMode = mode)
                    }

                    renderVisibleGrid()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::googleMap.isInitialized &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableLiveLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}