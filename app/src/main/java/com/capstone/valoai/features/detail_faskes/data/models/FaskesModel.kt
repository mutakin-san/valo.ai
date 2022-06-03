package com.capstone.valoai.features.detail_faskes.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FaskesModel(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String,
    val availableVaccineType: List<String>,
) : Parcelable