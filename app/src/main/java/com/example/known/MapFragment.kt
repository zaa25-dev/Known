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
import androidx.lifecycle.lifecycleScope
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapColorScheme
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

data class CellData(
    val cellId: String,
    var visitCount: Int,
    var lastMode: String,
    val firstVisitedTimestamp: Long = System.currentTimeMillis(),
    var lastVisitedTimestamp: Long = System.currentTimeMillis()
)

data class BonusCell(
    val cellId: String,
    val expiresAt: Long = System.currentTimeMillis() + (45 * 60 * 1000L)
)

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isFirstLocationFix = true

    private val renderedPolygons = mutableMapOf<String, Polygon>()
    private val visitedCells = mutableMapOf<String, CellData>()
    private var lastCellId: String? = null

    private val activeBonusCells = mutableMapOf<String, BonusCell>()
    private val renderedBonusPolygons = mutableMapOf<String, Polygon>()


    // need location permissions
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

        // Load saved cells FIRST so visitedCells is ready before the first GPS fix arrives
        viewLifecycleOwner.lifecycleScope.launch {
            val saved = FirebaseRepository.loadVisitedCells()
            if (saved.isNotEmpty()) {
                visitedCells.putAll(saved)
                renderVisibleGrid()
            }
            checkAndRequestPermissions()
        }

        googleMap.setOnCameraIdleListener {
            renderVisibleGrid()
        }

        // Tap the square to see details about it.
        // Will add: Points this specific square has given you and how many times you've visited it.
        googleMap.setOnPolygonClickListener { polygon ->
            val tag = polygon.tag as? String ?: return@setOnPolygonClickListener

            // 1. Handle Gold Bonus Square tap
            if (tag.startsWith("BONUS:")) {
                val cellId = tag.removePrefix("BONUS:")
                val bonus = activeBonusCells[cellId] ?: return@setOnPolygonClickListener
                val minsLeft = ((bonus.expiresAt - System.currentTimeMillis()) / 60_000L).coerceAtLeast(1L)

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("2.5x High-Value Bonus Zone")
                    .setMessage(
                        "Reward: +250 XP\n" +
                                "Time Remaining: ~$minsLeft mins\n\n" +
                                "Travel into this gold square before it expires to claim the bonus."
                    )
                    .setNegativeButton("Close", null)
                    .setPositiveButton("Navigate") { _, _ ->
                        openGoogleMapsNavigation(cellId)
                    }
                    .show()
                return@setOnPolygonClickListener
            }

            // 2. Handle already-explored purple/silver square tap
            val data = visitedCells[tag] ?: return@setOnPolygonClickListener
            val dateStr = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.US)
                .format(Date(data.firstVisitedTimestamp))

            val tierLabel = when {
                data.visitCount == 1 -> "Newly Discovered (Tier 1)"
                data.visitCount in 2..3 -> "Familiar Route (Tier 2)"
                data.visitCount in 4..6 -> "Frequent Territory (Tier 3)"
                else -> "Mastered Zone (Tier 4)"
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Zone $tag")
                .setMessage(
                    "Status: $tierLabel\n" +
                            "Total Visits: ${data.visitCount}\n" +
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
                        .strokeWidth(0f)
                        .clickable(true)
                )
                polygon.tag = cellId
                applyCoverageStyle(polygon, cellData.visitCount)
                renderedPolygons[cellId] = polygon
            }
        }

        renderBonusGrid(bounds)
    }

    private fun renderBonusGrid(bounds: LatLngBounds) {
        val iterator = renderedBonusPolygons.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val corners = GridHelper.getCellCorners(entry.key)
            val isOnScreen = corners.any { bounds.contains(it) }
            if (!isOnScreen || entry.key !in activeBonusCells) {
                entry.value.remove()
                iterator.remove()
            }
        }

        for (cellId in activeBonusCells.keys) {
            val corners = GridHelper.getCellCorners(cellId)
            val isOnScreen = corners.any { bounds.contains(it) }
            if (!isOnScreen || cellId in renderedBonusPolygons) continue

            val polygon = googleMap.addPolygon(
                PolygonOptions()
                    .addAll(corners)
                    .fillColor(0x66FFD54F.toInt())    // Translucent Gold fill
                    .strokeColor(0xFFFFB300.toInt())  // Amber-Gold border
                    .strokeWidth(4f)
                    .clickable(true)
            )
            polygon.tag = "BONUS:$cellId"
            renderedBonusPolygons[cellId] = polygon
        }
    }

    private fun ensureBonusSquaresAround(lat: Double, lng: Double) {
        val now = System.currentTimeMillis()

        val expiredIds = activeBonusCells.filterValues { it.expiresAt <= now }.keys
        expiredIds.forEach { activeBonusCells.remove(it) }

        var attempts = 0
        while (activeBonusCells.size < 3 && attempts < 20) {
            attempts++

            val distanceMeters = 200.0 + (Math.random() * 400.0)
            val bearingRad = Math.random() * 2.0 * Math.PI

            val dLat = (distanceMeters * cos(bearingRad)) / 111_320.0
            val dLng = (distanceMeters * sin(bearingRad)) / (111_320.0 * cos(Math.toRadians(lat)))

            val candidateCellId = GridHelper.getCellId(lat + dLat, lng + dLng)

            if (candidateCellId !in visitedCells && candidateCellId !in activeBonusCells) {
                activeBonusCells[candidateCellId] = BonusCell(cellId = candidateCellId)
            }
        }
    }

    // Different colors for how many times you've visited a certain square.
    private fun applyCoverageStyle(polygon: Polygon, visits: Int) {
        polygon.fillColor = when {
            visits == 1 -> 0x55B0BEC5.toInt()     // Soft Silver (1 visit)
            visits in 2..3 -> 0x66CE93D8.toInt()  // Light Purple (2-3 visits)
            visits in 4..6 -> 0x77AB47BC.toInt()  // Medium Purple (4-6 visits)
            else -> 0x886A1B9A.toInt()            // Deep Purple (7+ visits)
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

    // Everytime you opened the map tab,it would award you points for doing nothing.
    // This requires that if you open the map, we won't award you points until you've started going
    // to other squares.
    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val currentCellId = GridHelper.getCellId(location.latitude, location.longitude)

                ensureBonusSquaresAround(location.latitude, location.longitude)

                // On launch, anchor the starting cell without awarding repeat XP
                if (isFirstLocationFix) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                    isFirstLocationFix = false
                    lastCellId = currentCellId

                    // Only count on launch if this starting cell has NEVER been discovered before
                    if (currentCellId !in visitedCells) {
                        recordCellEntry(currentCellId)
                    } else {
                        renderVisibleGrid()
                    }
                    return
                }

                // Award XP & increment visits only when moving into a different square
                if (currentCellId != lastCellId) {
                    lastCellId = currentCellId
                    recordCellEntry(currentCellId)
                }
            }
        }
    }

    private fun recordCellEntry(cellId: String) {
        val now = System.currentTimeMillis()
        val existing = visitedCells[cellId]
        val isNewDiscovery = (existing == null)
        val claimedBonus = activeBonusCells.remove(cellId) != null

        if (existing != null) {
            existing.visitCount += 1
            existing.lastVisitedTimestamp = now
        } else {
            visitedCells[cellId] = CellData(
                cellId = cellId,
                visitCount = 1,
                lastMode = "EXPLORE",
                firstVisitedTimestamp = now,
                lastVisitedTimestamp = now
            )
        }

        val xpEarned = when {
            claimedBonus -> 250
            isNewDiscovery -> 100
            else -> 25
        }

        if (claimedBonus) {
            Toast.makeText(requireContext(), "Bonus Zone Claimed! +250 XP", Toast.LENGTH_SHORT).show()
        }

        val reward = GridHelper.VisitReward(
            xpEarned = xpEarned,
            isNewDiscovery = isNewDiscovery,
            isSpecialBonus = claimedBonus
        )

        // Sync cell visit & XP to Firestore in the background
        FirebaseRepository.recordCellVisit(
            cellId = cellId,
            mode = "EXPLORE",
            reward = reward
        )

        renderVisibleGrid()
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

    // I see a high value square on the map and I want to claim it.
    // Open Google Maps and navigate me to it so I can claim it.
    private fun openGoogleMapsNavigation(cellId: String) {
        val center = GridHelper.getCellCenter(cellId)
        val navUri = android.net.Uri.parse("google.navigation:q=${center.latitude},${center.longitude}")
        val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, navUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Fallback for devices without Google Maps installed
            val webUri = android.net.Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${center.latitude},${center.longitude}"
            )
            startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, webUri))
        }
    }

    override fun onPause() {
        super.onPause()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}