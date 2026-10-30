package com.example.data.cloud

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "CloudSyncManager"

data class CloudBackupPayload(
    val userId: String,
    val currentStreak: Int,
    val totalXp: Int,
    val completedTopicIds: List<Int>,
    val rolloverTopicIds: List<Int>,
    val freezesUsed: Int,
    val bestStreak: Int,
    val level: Int,
    val rankTitle: String,
    val lastActiveDate: String,
    val freezeShieldAvailable: Boolean,
    val estimatedMasteredHours: Double,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

class CloudSyncManager(private val context: Context) {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val credentialManager by lazy { CredentialManager.create(context) }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fbAuth ->
            trySend(fbAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Interactive Google Sign-In via Jetpack Credential Manager.
     */
    suspend fun signInWithGoogle(): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val serverClientId = context.getString(R.string.default_web_client_id)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Firebase user is null after sign in."))
                }
            } else {
                Result.failure(Exception("Unrecognized credential type returned."))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In was cancelled by the user")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed Google Sign-In flow", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    /**
     * Upload local study progress and streak state to Firestore.
     */
    suspend fun uploadBackup(payload: CloudBackupPayload): Result<Unit> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext Result.failure(
            IllegalStateException("User must be signed in to back up data.")
        )

        try {
            val docRef = firestore.collection("users")
                .document(uid)
                .collection("backups")
                .document("latest")

            val dataMap = hashMapOf<String, Any>(
                "userId" to uid,
                "currentStreak" to payload.currentStreak,
                "totalXp" to payload.totalXp,
                "completedTopicIds" to payload.completedTopicIds,
                "rolloverTopicIds" to payload.rolloverTopicIds,
                "freezesUsed" to payload.freezesUsed,
                "bestStreak" to payload.bestStreak,
                "level" to payload.level,
                "rankTitle" to payload.rankTitle,
                "lastActiveDate" to payload.lastActiveDate,
                "freezeShieldAvailable" to payload.freezeShieldAvailable,
                "estimatedMasteredHours" to payload.estimatedMasteredHours,
                "createdAt" to payload.createdAt,
                "updatedAt" to Timestamp.now()
            )

            docRef.set(dataMap, SetOptions.merge()).await()
            Log.d(TAG, "Progress successfully backed up to Firestore for user $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload progress to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieve the latest cloud backup from Firestore.
     */
    suspend fun fetchLatestBackup(): Result<CloudBackupPayload?> = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext Result.failure(
            IllegalStateException("User not authenticated.")
        )

        try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("backups")
                .document("latest")
                .get()
                .await()

            if (!snapshot.exists()) {
                return@withContext Result.success(null)
            }

            val data = snapshot.data ?: return@withContext Result.success(null)
            val currentStreak = (data["currentStreak"] as? Long)?.toInt() ?: 0
            val totalXp = (data["totalXp"] as? Long)?.toInt() ?: 0
            @Suppress("UNCHECKED_CAST")
            val completedTopicIds = (data["completedTopicIds"] as? List<*>)?.mapNotNull { (it as? Long)?.toInt() } ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val rolloverTopicIds = (data["rolloverTopicIds"] as? List<*>)?.mapNotNull { (it as? Long)?.toInt() } ?: emptyList()
            val freezesUsed = (data["freezesUsed"] as? Long)?.toInt() ?: 0
            val bestStreak = (data["bestStreak"] as? Long)?.toInt() ?: 0
            val level = (data["level"] as? Long)?.toInt() ?: 1
            val rankTitle = data["rankTitle"] as? String ?: "Rank #1 Contender"
            val lastActiveDate = data["lastActiveDate"] as? String ?: ""
            val freezeShieldAvailable = data["freezeShieldAvailable"] as? Boolean ?: true
            val estimatedMasteredHours = (data["estimatedMasteredHours"] as? Number)?.toDouble() ?: 0.0

            val backup = CloudBackupPayload(
                userId = uid,
                currentStreak = currentStreak,
                totalXp = totalXp,
                completedTopicIds = completedTopicIds,
                rolloverTopicIds = rolloverTopicIds,
                freezesUsed = freezesUsed,
                bestStreak = bestStreak,
                level = level,
                rankTitle = rankTitle,
                lastActiveDate = lastActiveDate,
                freezeShieldAvailable = freezeShieldAvailable,
                estimatedMasteredHours = estimatedMasteredHours
            )
            Result.success(backup)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch cloud backup", e)
            Result.failure(e)
        }
    }
}
