package com.capstone.valoai.features.auth.domain.usecases

import com.capstone.valoai.features.auth.data.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore

object UserServices {
    fun getDataUser(uid: String, db: FirebaseFirestore, onSuccess: (user: UserModel) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    it.data?.let { user ->
                        onSuccess(UserModel(name = user["name"].toString()))
                    }
                }
            }
    }
}