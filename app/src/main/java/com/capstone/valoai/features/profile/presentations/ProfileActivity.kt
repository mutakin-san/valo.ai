package com.capstone.valoai.features.profile.presentations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.capstone.valoai.R
import com.capstone.valoai.commons.Status
import com.capstone.valoai.databinding.ActivityProfileBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.auth.presentation.register.RegisterActivity
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.capstone.valoai.features.profile.data.remote.UserDataSourceRemote
import com.capstone.valoai.features.profile.domain.vmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val user: FirebaseUser? by lazy { auth.currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnLogout.setOnClickListener {
                auth.signOut()
            }

            back.setOnClickListener {
                finish()
            }

            user?.let {
                val dataSource = UserDataSourceRemote(it)
                val viewModel = ProfileViewModel(dataSource)
                viewModel.getProfile().observe(this@ProfileActivity) {
                    it.let { st ->
                        when (st.status) {
                            Status.SUCCESS -> {
                                nameProfileText.text = st.data?.name ?: "none"
                                dateProfileText.text = st.data?.birthDate ?: "none"
                                Glide.with(baseContext).load(user?.photoUrl).circleCrop()
                                    .into(profileImage)
                                hideProgressBar()
                            }
                            Status.ERROR -> {
                                Log.println(Log.ERROR, "getProfile:failure", "fail get data")
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Fail get profile",
                                    Toast.LENGTH_SHORT
                                ).show()

                                nameProfileText.text = user?.displayName ?: "none"
                                dateProfileText.text = "none"
                                Glide.with(baseContext).load(user?.photoUrl).circleCrop()
                                    .into(profileImage)
                                hideProgressBar()

                            }
                            Status.LOADING -> {
                                showProgressBar()

                            }
                        }
                    }
                }

            }


        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
                return@addAuthStateListener
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}