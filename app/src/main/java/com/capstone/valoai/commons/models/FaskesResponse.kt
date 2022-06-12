package com.capstone.valoai.commons.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FaskesResponse(
    @field:SerializedName("id")
    val id: String,

	@field:SerializedName("name")
	val name: String,

    @field:SerializedName("latitude")
    val latitude: String,

	@field:SerializedName("longitude")
	val longitude: String,

	@field:SerializedName("vac1")
	val vac1: String,

	@field:SerializedName("vac2")
	val vac2: String,

	@field:SerializedName("booster")
	val booster: String,

    @field:SerializedName("moderna")
    val moderna: String,

    @field:SerializedName("sinopharm")
    val sinopharm: String,

    @field:SerializedName("astrazaneca")
    val astrazaneca: String,


    @field:SerializedName("pfizer")
    val pfizer: String,

    @field:SerializedName("sinovac")
    val sinovac: String,


    @field:SerializedName("janssen")
    val janssen: String,

) : Parcelable
