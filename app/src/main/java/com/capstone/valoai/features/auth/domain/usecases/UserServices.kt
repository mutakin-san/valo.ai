package com.capstone.valoai.features.auth.domain.usecases

import com.capstone.valoai.features.profile.data.models.Profile
import com.google.firebase.firestore.FirebaseFirestore

object UserServices {
    fun getDataUser(uid: String, db: FirebaseFirestore, onSuccess: (user: Profile) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    it.data?.let { user ->
                        onSuccess(
                            Profile(
                                name = user["name"].toString(),
                                birthDate = user["birthDate"].toString()
                            )
                        )
                    }
                }
            }
    }
}