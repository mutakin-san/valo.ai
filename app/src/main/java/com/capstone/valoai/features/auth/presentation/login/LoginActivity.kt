package com.capstone.valoai.features.auth.presentation.login

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.valoai.MainActivity
import com.capstone.valoai.R
import com.capstone.valoai.databinding.ActivityLoginBinding
import com.capstone.valoai.features.auth.data.local.UserPref
import com.capstone.valoai.features.auth.data.local.userDatastore
import com.capstone.valoai.features.auth.data.models.UserModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val userPref: UserPref by lazy {
        UserPref(this.userDatastore)
    }
    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val oneTapSignInRegister =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                handleSignInResult(result.data)
            }
        }


    private fun handleSignInResult(data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                lifecycleScope.launch {
                                    userPref.setUser(UserModel(name = task.result.user?.displayName))
                                }
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                            } else {
                                Log.e(TAG, "${task.exception?.localizedMessage}")
                            }
                        }
                }
                else -> {
                    Log.d(TAG, "No ID token or password!")
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(TAG, "One-tap dialog was closed.")
                    // Don't re-prompt the user.
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(TAG, "One-tap encountered a network error.")
                    // Try again or just ignore.
                }
                else -> {
                    Log.d(
                        TAG, "Couldn't get credential from result." +
                                " (${e.localizedMessage})"
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        binding.signInWithGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        oneTapSignInRegister.launch(
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage as String)
                }
        }

        binding.btnLogin.setOnClickListener {
            firebaseAuth.signInWithEmailAndPassword(
                "mutakin@gmail.com",
                "123456"
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    lifecycleScope.launch {
                        userPref.setUser(UserModel(name = it.result.user?.displayName))
                    }
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }
}