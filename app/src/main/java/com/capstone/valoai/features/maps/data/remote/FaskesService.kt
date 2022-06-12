package com.capstone.valoai.features.maps.data.remote

import com.capstone.valoai.commons.models.FaskesResponse
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import retrofit2.Response
import retrofit2.http.GET

interface FaskesService {
    @GET("/faskes")
    suspend fun getAllFaskes(): Response<List<FaskesResponse>>
}