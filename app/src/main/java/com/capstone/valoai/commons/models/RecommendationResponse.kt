package com.capstone.valoai.commons.models

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("recommendations")
	val recommendations: List<String>
)
