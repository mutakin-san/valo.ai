package com.capstone.valoai.features.profile.data.remote

import android.util.Log
import com.capstone.valoai.commons.PathFire
import com.capstone.valoai.features.profile.data.models.Profile
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import java.lang.Exception

class UserDataSourceRemote(private val user: FirebaseUser) {
    private val db by lazy { FirebaseFirestore.getInstance() }
    suspend fun getProfile(): Profile? {
        val def = CompletableDeferred<Profile?>()
        try {
            db.collection(PathFire.users).document(user.uid).get().addOnCompleteListener {
                if (it.isSuccessful)
                    it.addOnCompleteListener { data ->
                        def.complete(
                            if (data.isSuccessful) Profile(
                                (data.result?.get("name") ?: "") as String,
                                (data.result?.get("birthDate") ?: "") as String,
                                (data.result?.get("riwayat1") ?: "") as String,
                                (data.result?.get("riwayat2") ?: "") as String,
                                (data.result?.get("riwayat3") ?: "") as String,
                            ) else null
                        )
                    }
                else def.complete(null)
            }
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Profile Log", e.message ?: "Get Profile Errors")
        }
        return def.await()
    }
}