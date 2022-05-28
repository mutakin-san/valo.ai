package com.capstone.valoai.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.capstone.valoai.R
import com.capstone.valoai.databinding.ActivityOnBoardingBinding
import com.capstone.valoai.ui.fragments.OnBoardingAdapter
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (position == OnBoardingAdapter.pages.size - 1){
                binding.dotIndicators.visibility = View.GONE
                binding.btnGetStarted.visibility = View.VISIBLE
            }else {
                binding.dotIndicators.visibility = View.VISIBLE
                binding.btnGetStarted.visibility = View.GONE
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.onboardingViewPager.adapter = OnBoardingAdapter(this, OnBoardingAdapter.pages.size)
        binding.onboardingViewPager.registerOnPageChangeCallback(pageChangeCallback)
        TabLayoutMediator(binding.dotIndicators, binding.onboardingViewPager) { tab, _ ->
            tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.default_dot_indicator, null)
            if(tab.isSelected){
                tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.active_dot_indicator, null)
            }
        }.attach()


        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        binding.onboardingViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }
}