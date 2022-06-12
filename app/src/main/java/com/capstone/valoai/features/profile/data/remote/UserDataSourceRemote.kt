package com.capstone.valoai.features.profile.data.remote

import android.net.Uri
import android.util.Log
import com.capstone.valoai.commons.PathFire
import com.capstone.valoai.features.profile.data.models.Profile
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import java.lang.Exception

class UserDataSourceRemote(private val user: FirebaseUser) {
    suspend fun getProfile(): Profile? {
        val def = CompletableDeferred<Profile?>()
        try {
            val db = FirebaseFirestore.getInstance()

            db.collection(PathFire.users).document(user.uid).get().addOnCompleteListener {
                if (it.isSuccessful)
                    it.addOnCompleteListener { data ->
                        Log.println(Log.INFO, "test", data.result["vaksin1"].toString() ?: "none")
                        def.complete(
                            if (data.isSuccessful) Profile(
                                name = (data.result?.get("name") ?: "") as String,
                                birthDate = (data.result?.get("birthDate") ?: "") as String,
                                vaksin1 = (data.result?.get("vaksin1") ?: "") as String,
                                vaksin2 = (data.result?.get("vaksin2") ?: "") as String,
                                vaksin3 = (data.result?.get("vaksin3") ?: "") as String,
                                tanggal_vaksin1 = (data.result?.get("tanggal_vaksin1")
                                    ?: "") as String,
                                tanggal_vaksin2 = (data.result?.get("tanggal_vaksin2")
                                    ?: "") as String,
                                tanggal_vaksin3 = (data.result?.get("tanggal_vaksin3")
                                    ?: "") as String,
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

    suspend fun putProfile(profile: Profile): Boolean {

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(profile.name)
            .setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"))
            .build()

        val def = CompletableDeferred<Boolean>()
        try {
            val db = FirebaseFirestore.getInstance()

            db.collection(PathFire.users).document(user.uid).set(mapOf(
                "name" to profile.name,
                "birthDate" to profile.birthDate,
                "vaksin1" to profile.vaksin1,
                "vaksin2" to profile.vaksin2,
                "vaksin3" to profile.vaksin3,
                "tanggal_vaksin1" to profile.tanggal_vaksin1,
                "tanggal_vaksin2" to profile.tanggal_vaksin2,
                "tanggal_vaksin3" to profile.tanggal_vaksin3
            )).addOnCompleteListener {
                if (it.isSuccessful) {
                    it.addOnCompleteListener { data -> def.complete(data.isSuccessful) }
                    user.updateProfile(profileUpdates).addOnCompleteListener {
                        def.complete(it.isSuccessful)
                    }
                } else def.complete(false)
            }
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Profile Log", e.message ?: "Put Profile Errors")
            def.complete(false)
        }
        return def.await()
    }

    suspend fun deleteProfile(): Boolean {
        val def = CompletableDeferred<Boolean>()
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection(PathFire.users).document(user.uid).delete()
                .addOnCompleteListener { fire ->
                    if (fire.isSuccessful) {
                        user.delete().addOnCompleteListener {
                            if (it.isSuccessful) {
                                fire.addOnCompleteListener { data -> def.complete(data.isSuccessful) }
                            } else {
                                def.complete(false)
                            }
                        }
                    } else def.complete(false)
                }
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Profile Log", e.message ?: "Put Profile Errors")
            def.complete(false)
        }
        return def.await()
    }
}