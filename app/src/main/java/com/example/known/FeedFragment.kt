package com.example.known

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.util.Locale

class FeedFragment : Fragment() {

    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var progressLeaderboard: ProgressBar
    private lateinit var tvEmptyLeaderboard: TextView
    private lateinit var tvRefresh: TextView
    private lateinit var btnOpenMapHvz: MaterialButton

    private val adapter = LeaderboardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvLeaderboard = view.findViewById(R.id.rv_leaderboard)
        progressLeaderboard = view.findViewById(R.id.progress_leaderboard)
        tvEmptyLeaderboard = view.findViewById(R.id.tv_empty_leaderboard)
        tvRefresh = view.findViewById(R.id.tv_refresh_leaderboard)
        btnOpenMapHvz = view.findViewById(R.id.btn_open_map_hvz)

        rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        rvLeaderboard.adapter = adapter
        rvLeaderboard.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        btnOpenMapHvz.setOnClickListener {
            // Switch bottom navigation to the Map tab
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav?.selectedItemId = R.id.map
        }

        tvRefresh.setOnClickListener {
            loadLeaderboard()
        }

        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        progressLeaderboard.visibility = View.VISIBLE
        tvEmptyLeaderboard.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val players = FirebaseRepository.getTopLeaderboard(limit = 15)
            progressLeaderboard.visibility = View.GONE

            if (players.isEmpty()) {
                tvEmptyLeaderboard.visibility = View.VISIBLE
                rvLeaderboard.visibility = View.GONE
            } else {
                tvEmptyLeaderboard.visibility = View.GONE
                rvLeaderboard.visibility = View.VISIBLE
                adapter.submitList(players, FirebaseRepository.getCurrentUser()?.uid)
            }
        }
    }

    private class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

        private var items: List<UserProfile> = emptyList()
        private var currentUserUid: String? = null

        fun submitList(newItems: List<UserProfile>, myUid: String?) {
            items = newItems
            currentUserUid = myUid
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.leaderboard, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val player = items[position]
            val rank = position + 1
            val isMe = player.uid == currentUserUid

            holder.tvRank.text = "#$rank"
            holder.tvPlayerName.text = if (isMe) "${player.username} (You)" else player.username
            holder.tvPlayerXp.text = String.format(Locale.US, "%,d XP", player.totalXp)

            holder.tvPlayerSubstats.text = if (player.isPublicStats || isMe) {
                String.format(
                    Locale.US,
                    "%d zones • %.1f mi",
                    player.zonesUnlocked,
                    player.milesLogged
                )
            } else {
                "Stats hidden by explorer"
            }

            // Aside from it saying (YOU), a highlight that shows you on the leaderboard.
            holder.itemView.setBackgroundColor(
                if (isMe) Color.parseColor("#F3EFFA") else Color.TRANSPARENT
            )
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvRank: TextView = view.findViewById(R.id.tv_rank)
            val tvPlayerName: TextView = view.findViewById(R.id.tv_player_name)
            val tvPlayerSubstats: TextView = view.findViewById(R.id.tv_player_substats)
            val tvPlayerXp: TextView = view.findViewById(R.id.tv_player_xp)
        }
    }
}