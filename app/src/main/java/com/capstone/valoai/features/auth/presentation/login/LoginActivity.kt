package com.capstone.valoai.features.auth.presentation.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.databinding.ActivityLoginBinding
import com.capstone.valoai.features.auth.presentation.register.FormPersonalActivity
import com.capstone.valoai.features.auth.presentation.register.RegisterActivity
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding: ActivityLoginBinding
        get() = _binding!!


    private lateinit var firebaseAuth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            startActivity(
                Intent(
                    this@LoginActivity,
                    DashboardActivity::class.java
                )
            )
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Buttons
        with(binding) {

            btnToRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
//                startActivity(Intent(this@LoginActivity, FormPersonalActivity::class.java))
                finish()
            }

            btnLogin.setOnClickListener {
                val email = fieldEmail.editText?.text.toString()
                val password = fieldPassword.editText?.text.toString()
                signIn(email, password)
            }

            signInWithGoogle.setOnClickListener {

                // Choose authentication providers
                val providers = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build())

                // Create and launch sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)

            }

        }

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }
    }

    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        showProgressBar()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed : ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressBar()
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.editText?.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.error = "Required."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.editText?.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        internal val TAG = LoginActivity::class.java.simpleName
    }
}