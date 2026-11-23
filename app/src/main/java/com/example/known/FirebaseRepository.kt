package com.example.known

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String = "",
    val username: String = "Explorer",
    val totalXp: Long = 0L,
    val zonesUnlocked: Long = 0L,
    val milesLogged: Double = 0.0,
    val bonusZonesClaimed: Long = 0L,
    val isPublicStats: Boolean = true
)

object FirebaseRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // In-memory cache of visited cells so MapFragment never has to re-fetch across tab switches
    private var cachedCells: MutableMap<String, CellData>? = null

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Sign in with Google using the native Android one.
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(request = request, context = context)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
                val user = auth.signInWithCredential(firebaseCred).await().user
                    ?: return Result.failure(Exception("Firebase user was null"))

                ensureUserDocumentExists(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Unexpected credential type"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if the user has already chosen a custom username ( to be added soon)
    suspend fun hasCompletedUsernameSetup(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.getBoolean("hasSetUsername") ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Your typical "this username was taken" prompt
    suspend fun claimCustomUsername(rawUsername: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not signed in"))
        val cleanName = rawUsername.trim()

        if (cleanName.length !in 3..20) {
            return Result.failure(Exception("Username must be 3–20 characters."))
        }
        if (!cleanName.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return Result.failure(Exception("Only letters, numbers, and underscores allowed."))
        }

        return try {
            val existing = db.collection("users")
                .whereEqualTo("usernameLower", cleanName.lowercase())
                .get()
                .await()

            val takenBySomeoneElse = existing.documents.any { it.id != uid }
            if (takenBySomeoneElse) {
                return Result.failure(Exception("That username is already taken."))
            }

            val updates = mapOf(
                "username" to cleanName,
                "usernameLower" to cleanName.lowercase(),
                "hasSetUsername" to true
            )
            db.collection("users").document(uid).set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all their information ready from the database for them
    suspend fun getCurrentUserProfile(): UserProfile? {
        val user = auth.currentUser ?: return null
        return try {
            val doc = db.collection("users").document(user.uid).get().await()
            if (doc.exists()) {
                UserProfile(
                    uid = doc.id,
                    username = doc.getString("username") ?: user.displayName ?: "Explorer",
                    totalXp = doc.getLong("totalXp") ?: 0L,
                    zonesUnlocked = doc.getLong("zonesUnlocked") ?: 0L,
                    milesLogged = doc.getDouble("milesLogged") ?: 0.0,
                    bonusZonesClaimed = doc.getLong("bonusZonesClaimed") ?: 0L,
                    isPublicStats = doc.getBoolean("isPublicStats") ?: true
                )
            } else {
                UserProfile(uid = user.uid, username = user.displayName ?: "Explorer")
            }
        } catch (e: Exception) {
            UserProfile(uid = user.uid, username = user.displayName ?: "Explorer")
        }
    }

    fun updatePublicStatsPreference(isPublic: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .set(mapOf("isPublicStats" to isPublic), SetOptions.merge())
    }

    fun signOut() {
        cachedCells = null
        auth.signOut()
    }

    // If you're a new user, you're added.
    private suspend fun ensureUserDocumentExists(user: FirebaseUser) {
        val userRef = db.collection("users").document(user.uid)
        val snapshot = userRef.get().await()
        if (!snapshot.exists()) {
            val initialProfile = hashMapOf(
                "uid" to user.uid,
                "username" to (user.displayName ?: "Explorer"),
                "hasSetUsername" to false,
                "totalXp" to 0L,
                "zonesUnlocked" to 0L,
                "milesLogged" to 0.0,
                "bonusZonesClaimed" to 0L,
                "isPublicStats" to true
            )
            userRef.set(initialProfile).await()
        }
    }

    // 3. Save a visited cell & increment user XP/counters in Firestore
    fun recordCellVisit(
        cellId: String,
        mode: String,
        reward: GridHelper.VisitReward,
        distanceMiles: Double = 0.093
    ) {
        val uid = auth.currentUser?.uid ?: return
        val now = System.currentTimeMillis()

        // Keep in-memory cache synchronized immediately (including lastVisitedTimestamp)
        cachedCells?.let { cache ->
            val current = cache[cellId]
            if (current != null) {
                current.visitCount += 1
                current.lastMode = mode
                current.lastVisitedTimestamp = now
            } else {
                cache[cellId] = CellData(
                    cellId = cellId,
                    visitCount = 1,
                    lastMode = mode,
                    firstVisitedTimestamp = now,
                    lastVisitedTimestamp = now
                )
            }
        }

        val userRef = db.collection("users").document(uid)
        val cellRef = userRef.collection("visited_cells").document(cellId)

        val cellUpdate = hashMapOf<String, Any>(
            "cellId" to cellId,
            "visitCount" to FieldValue.increment(1),
            "lastMode" to mode,
            "lastVisitedTimestamp" to now
        )
        if (reward.isNewDiscovery) {
            cellUpdate["firstVisitedTimestamp"] = now
        }
        cellRef.set(cellUpdate, SetOptions.merge())

        if (reward.xpEarned > 0) {
            val userUpdates = mutableMapOf<String, Any>(
                "totalXp" to FieldValue.increment(reward.xpEarned.toLong()),
                "milesLogged" to FieldValue.increment(distanceMiles)
            )
            if (reward.isNewDiscovery) {
                userUpdates["zonesUnlocked"] = FieldValue.increment(1)
            }
            if (reward.isSpecialBonus) {
                userUpdates["bonusZonesClaimed"] = FieldValue.increment(1)
            }
            userRef.set(userUpdates, SetOptions.merge())
        }
    }

    // 4. Load all visited cells for the signed-in user
    suspend fun loadVisitedCells(forceRefresh: Boolean = false): Map<String, CellData> {
        if (!forceRefresh && cachedCells != null) {
            return cachedCells!!
        }
        val uid = auth.currentUser?.uid ?: return emptyMap()
        return try {
            val snapshot = db.collection("users").document(uid)
                .collection("visited_cells")
                .get()
                .await()

            val loaded = snapshot.documents.associate { doc ->
                val id = doc.id
                val visits = (doc.getLong("visitCount") ?: 1L).toInt()
                val mode = doc.getString("lastMode") ?: "WALK"
                val firstTs = doc.getLong("firstVisitedTimestamp") ?: System.currentTimeMillis()
                val lastTs = doc.getLong("lastVisitedTimestamp") ?: firstTs
                id to CellData(
                    cellId = id,
                    visitCount = visits,
                    lastMode = mode,
                    firstVisitedTimestamp = firstTs,
                    lastVisitedTimestamp = lastTs
                )
            }.toMutableMap()

            cachedCells = loaded
            loaded
        } catch (e: Exception) {
            cachedCells ?: emptyMap()
        }
    }

    // 5. Fetch top explorers for the Feed Leaderboard
    suspend fun getTopLeaderboard(limit: Long = 10): List<UserProfile> {
        return try {
            val snapshot = db.collection("users")
                .orderBy("totalXp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                UserProfile(
                    uid = doc.id,
                    username = doc.getString("username") ?: "Explorer",
                    totalXp = doc.getLong("totalXp") ?: 0L,
                    zonesUnlocked = doc.getLong("zonesUnlocked") ?: 0L,
                    milesLogged = doc.getDouble("milesLogged") ?: 0.0,
                    bonusZonesClaimed = doc.getLong("bonusZonesClaimed") ?: 0L,
                    isPublicStats = doc.getBoolean("isPublicStats") ?: true
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}