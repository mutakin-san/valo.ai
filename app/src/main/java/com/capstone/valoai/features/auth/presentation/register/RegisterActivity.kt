package com.capstone.valoai.features.auth.presentation.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.commons.navigateTo
import com.capstone.valoai.databinding.ActivityRegisterBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            btnToLogin.setOnClickListener {
                navigateTo(this@RegisterActivity, LoginActivity::class.java)
            }
            checkboxRegister.setOnClickListener {
                checkboxRegister.error = null
            }
            btnRegister.setOnClickListener {
                val email = fieldName.editText?.text.toString()
                val password = fieldPassword.editText?.text.toString()
                val alreadyVaksin = checkboxRegister.isChecked
                register(email, password, alreadyVaksin)
            }
        }

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
    }

    private fun register(email: String, password: String, alreadyVaksin: Boolean) {
        if (!validateForm()) return

        showProgressBar()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                db.collection("users").document(firebaseAuth.currentUser?.uid ?: "").set(
                    mapOf(
                        "alreadyVaksin" to alreadyVaksin,
                        "email" to email
                    )
                ).addOnCompleteListener { tk ->
                    if (tk.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        navigateTo(this@RegisterActivity, FormPersonalActivity::class.java)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserEmail:failure", task.exception)
                        Toast.makeText(
                            this@RegisterActivity,
                            "Authentication failed : ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserEmail:failure", task.exception)
                Toast.makeText(
                    this@RegisterActivity,
                    "Authentication failed : ${task.exception?.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            hideProgressBar()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldName.editText?.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldName.error = "Required."
            valid = false
        } else {
            binding.fieldName.error = null
        }

        val password = binding.fieldPassword.editText?.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        val confirPassword = binding.fieldPasswordConfirmation.editText?.text.toString()
        when {
            TextUtils.isEmpty(confirPassword) -> {
                binding.fieldPasswordConfirmation.error = "Required."
                valid = false
            }
            password != confirPassword -> {
                binding.fieldPasswordConfirmation.error = "Password Tidak Sama"
                valid = false
            }
            else -> {
                binding.fieldPasswordConfirmation.error = null
            }
        }

        val checkBoxTermsAndPolicy = binding.checkboxRegister.isChecked
        if (!checkBoxTermsAndPolicy) {
            binding.checkboxRegister.error = "Required."
            valid = false
        } else {
            binding.checkboxRegister.error = null
        }

        return valid
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    companion object {
        internal val TAG = RegisterActivity::class.java.simpleName
    }
}