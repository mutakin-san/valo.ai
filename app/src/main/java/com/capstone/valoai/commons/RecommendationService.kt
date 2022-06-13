package com.capstone.valoai.commons

import com.capstone.valoai.commons.models.RecommendationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RecommendationService {
    @GET("/recommendations/{type}")
    suspend fun getRecommendationVaccineType(@Path("type") type: String): Response<RecommendationResponse>
}