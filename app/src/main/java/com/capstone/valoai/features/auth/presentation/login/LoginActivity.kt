package com.capstone.valoai.features.auth.presentation.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R
import com.capstone.valoai.commons.hideProgressBar
import com.capstone.valoai.commons.showProgressBar
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
            val navigateTo = if(result.idpResponse?.isNewUser == true){
                FormPersonalActivity::class.java
            }else{
                DashboardActivity::class.java
            }
            val mIntent = Intent(this@LoginActivity, navigateTo)
            startActivity(mIntent)
            finish()
        } else {
            Toast.makeText(
                this@LoginActivity,
                result.idpResponse?.error?.localizedMessage ?: getString(R.string.google_signin_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            btnToRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
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
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                // Create and launch sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)

            }

        }

        firebaseAuth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
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

        showProgressBar(binding.progressBar)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressBar(binding.progressBar)
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



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}