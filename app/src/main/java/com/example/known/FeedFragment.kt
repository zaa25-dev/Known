package com.example.known

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Locale

class FeedFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvRankTitle: TextView
    private lateinit var tvXpFraction: TextView
    private lateinit var progressXp: LinearProgressIndicator

    private lateinit var tvStatKnownPct: TextView
    private lateinit var tvStatHexes: TextView
    private lateinit var tvStatMiles: TextView
    private lateinit var tvStatHvz: TextView

    private lateinit var switchPublicStats: MaterialSwitch
    private lateinit var switchBackgroundTracking: MaterialSwitch
    private lateinit var toggleUnits: MaterialButtonToggleGroup
    private lateinit var btnClearMapData: MaterialButton

    // sample numbers for now to show how it looks
    private var totalMilesLogged = 312.4
    private var useMiles = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        loadSavedSettings()
        setupListeners()
    }

    private fun bindViews(view: View) {

        tvStatKnownPct = view.findViewById(R.id.tv_stat_known_pct)
        tvStatHexes = view.findViewById(R.id.tv_stat_hexes)
        tvStatMiles = view.findViewById(R.id.tv_stat_miles)
        tvStatHvz = view.findViewById(R.id.tv_stat_hvz)

        switchPublicStats = view.findViewById(R.id.switch_public_stats)
        switchBackgroundTracking = view.findViewById(R.id.switch_background_tracking)
        toggleUnits = view.findViewById(R.id.toggle_units)
        btnClearMapData = view.findViewById(R.id.btn_clear_map_data)
    }

    private fun loadSavedSettings() {
        val prefs = requireContext().getSharedPreferences("known_prefs", Context.MODE_PRIVATE)
        switchPublicStats.isChecked = prefs.getBoolean("share_public_stats", true)
        switchBackgroundTracking.isChecked = prefs.getBoolean("background_tracking", false)
        useMiles = prefs.getBoolean("use_miles", true)

        toggleUnits.check(if (useMiles) R.id.btn_unit_miles else R.id.btn_unit_km)
        refreshDistanceDisplay()
    }

    private fun setupListeners() {
        val prefs = requireContext().getSharedPreferences("known_prefs", Context.MODE_PRIVATE)


        switchBackgroundTracking.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("background_tracking", isChecked).apply()
            // TODO: Start or stop your foreground location tracking service
        }

        toggleUnits.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                useMiles = (checkedId == R.id.btn_unit_miles)
                prefs.edit().putBoolean("use_miles", useMiles).apply()
                refreshDistanceDisplay()
            }
        }

        btnClearMapData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reset Local Map History?")
                .setMessage("This will clear your visited grid squares and local route trails on this device.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset") { _, _ ->
                    // TODO: Clear Room database tables
                    updateStats(xp = 0, cellsUnlocked = 0, miles = 0.0, bonusZones = 0)
                }
                .show()
        }
    }

    private fun refreshDistanceDisplay() {
        tvStatMiles.text = if (useMiles) {
            String.format(Locale.US, "%.1f mi", totalMilesLogged)
        } else {
            String.format(Locale.US, "%.1f km", totalMilesLogged * 1.60934)
        }
    }

    // call from room when thats created
    fun updateStats(xp: Int, cellsUnlocked: Int, miles: Double, bonusZones: Int) {
        val level = (xp / 1000) + 1
        val currentLevelXp = xp % 1000
        val knownPct = (cellsUnlocked / 500.0) * 100.0

        tvRankTitle.text = "Level $level • Local Pathfinder"
        tvXpFraction.text = "$currentLevelXp / 1,000 XP"
        progressXp.progress = currentLevelXp

        tvStatKnownPct.text = String.format(Locale.US, "%.1f%%", knownPct)
        tvStatHexes.text = cellsUnlocked.toString()
        tvStatHvz.text = bonusZones.toString()

        totalMilesLogged = miles
        refreshDistanceDisplay()
    }
}