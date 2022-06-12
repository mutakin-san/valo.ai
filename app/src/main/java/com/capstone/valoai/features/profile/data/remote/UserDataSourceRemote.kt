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
    private val db by lazy { FirebaseFirestore.getInstance() }
    suspend fun getProfile(): Profile? {
        val def = CompletableDeferred<Profile?>()
        try {
            db.collection(PathFire.users).document(user.uid).get().addOnCompleteListener {
                if (it.isSuccessful)
                    it.addOnCompleteListener { data ->
                        def.complete(
                            if (data.isSuccessful) Profile(
                                name = (data.result?.get("name") ?: "") as String,
                                birthDate = (data.result?.get("birthDate") ?: "") as String,
                                riwayat1 = (data.result?.get("vaksin1") ?: "") as String,
                                riwayat2 = (data.result?.get("vaksin2") ?: "") as String,
                                riwayat3 = (data.result?.get("vaksin3") ?: "") as String,
                                tanggalRiwayat1 = (data.result?.get("tanggal_vaksin1")
                                    ?: "") as String,
                                tanggalRiwayat2 = (data.result?.get("tanggal_vaksin2")
                                    ?: "") as String,
                                tanggalRiwayat3 = (data.result?.get("tanggal_vaksin3")
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
            val data = Profile(
                name = profile.name,
                birthDate = profile.birthDate,
                riwayat1 = profile.riwayat1,
                riwayat2 = profile.riwayat2,
                riwayat3 = profile.riwayat3,
                tanggalRiwayat1 = profile.tanggalRiwayat1,
                tanggalRiwayat2 = profile.tanggalRiwayat2,
                tanggalRiwayat3 = profile.tanggalRiwayat3,
            )

            db.collection(PathFire.users).document(user.uid).set(data).addOnCompleteListener {
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

    suspend fun deleteProfile(profile: String): Boolean {
        val def = CompletableDeferred<Boolean>()
        try {
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