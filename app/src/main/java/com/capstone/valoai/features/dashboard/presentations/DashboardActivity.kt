package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.valoai.commons.ApiConfig
import com.capstone.valoai.commons.Status
import com.capstone.valoai.databinding.ActivityDashboardBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter.OnItemClickCallback
import com.capstone.valoai.features.dashboard.domain.adapter.RiwayatListAdapter
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.detail_faskes.presentation.DetailFaskesActivity
import com.capstone.valoai.features.maps.data.FaskesRepository
import com.capstone.valoai.features.maps.domain.usecase.FaskesViewModel
import com.capstone.valoai.features.maps.domain.usecase.ViewModelFactory
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    private lateinit var viewModel: FaskesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        this.title = "Dashboard"
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        attachFikesList()
        setContentView(binding.root)

        val bottomAppBar = binding.bottomAppBar
        val bottomBarBackground = bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(RoundedCornerTreatment()).setAllCornerSizes(RelativeCornerSize(0.1f))
            .build()

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        with(binding) {
            txtName.text = user?.displayName
            Glide.with(baseContext).load(user?.photoUrl).circleCrop().into(profileDashboard)


            bottomNavigationView.setOnItemSelectedListener { item ->
//            Log.println(Log.INFO, "Test", "${item.title}")
                when (item.title) {
                    "Home" -> attachFikesList()
                    "Riwayat" -> attachHistoryList()
                }
                true
            }

            fab.setOnClickListener {
                onClickFeb()
            }

            firebaseAuth.addAuthStateListener {
                if (it.currentUser == null) {
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    finish()
                }
            }

            profileDashboard.setOnClickListener {
                firebaseAuth.signOut()
            }
        }


    }

    private fun attachFikesList() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(FaskesRepository(ApiConfig.faskesService))
            )[FaskesViewModel::class.java]

        viewModel.getAllFaskes().observe(this){
            it?.let {resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        val adapter = FakesListAdapter(resource.data ?: ArrayList())
                        with(binding) {
                            dashboardList.layoutManager = layoutManager
                            adapter.setOnItemClickCallback(object : OnItemClickCallback {
                                override fun onItemClicked(data: FaskesModel) {
                                    goToDetailFakes(data)
                                }
                            })
                            dashboardList.adapter = adapter
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
//                        progressBar.visibility = View.VISIBLE
//                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun goToDetailFakes(data: FaskesModel){
        val intentToDetail =
            Intent(this@DashboardActivity, DetailFaskesActivity::class.java)
        intentToDetail.apply {
            putExtra(
                DetailFaskesActivity.FASKES_EXTRA_NAME,
                data
            )
        }
        startActivity(intentToDetail)
    }

    private fun attachHistoryList() {
        val dataDummy = arrayListOf("Test3", "Test5")
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = RiwayatListAdapter(dataDummy)
        with(binding) {
            dashboardList.layoutManager = layoutManager
            adapter.setOnItemClickCallback(object : RiwayatListAdapter.OnItemClickCallback{
                override fun onItemClicked(data: String) {
                }
            })
            dashboardList.adapter = adapter
        }
    }

    fun onClickFeb() {
        Log.println(Log.INFO, "test", assets.list("src/main/asserts/dataset").toString())
//        loadModelFile()
//        startActivity(Intent(this@DashboardActivity, VaksinLocationMapsActivity::class.java))
    }


    @Throws(IOException::class)
    private fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer? {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}