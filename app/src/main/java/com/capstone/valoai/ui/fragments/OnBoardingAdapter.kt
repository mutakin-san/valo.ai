package com.capstone.valoai.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.valoai.R
import com.capstone.valoai.data.models.OnBoardingModel

class OnBoardingAdapter(activity: AppCompatActivity, private val itemsCount: Int) :
    FragmentStateAdapter(activity) {
    companion object {

        val pages: List<OnBoardingModel> = listOf(
            OnBoardingModel(
                R.drawable.page1,
                "Temukan Lokasi Vaksin \n" +
                        "Dengan Mudah",
                "Valo.Ai bisa membantu kamu menemukan lokasi vaksin terdekat dengan kamu"
            ),
            OnBoardingModel(
                R.drawable.page2,
                "Rekomendasi jenis Vaksin\n" +
                        "yang cocok buat kamu",
                "Valo.Ai bisa membantu kamu mengetahui\n" +
                        "jenis vaksin yang cocok buat kamu dan menemukan lokasi vaksin yang sesuai rekomendasi."
            ),
            OnBoardingModel(
                R.drawable.page3,
                "Menampilkan lokasi vaksin\n" +
                        "dengan map",
                "Valo.Ai bisa membantu kamu melihat lokasi pada map."
            ),
            OnBoardingModel(
                R.drawable.ic_logo,
                "Selamat Datang\nDi Valo.Ai",
                ""
            ),
        )
    }

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return OnBoardingItemFragment.newInstance(pages[position])
    }
}