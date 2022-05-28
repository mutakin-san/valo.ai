package com.capstone.valoai.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnBoardingModel(val imageId: Int, val title: String, val description: String) :
    Parcelable