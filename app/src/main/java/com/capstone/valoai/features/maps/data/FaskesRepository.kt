package com.capstone.valoai.features.maps.data

import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.maps.data.remote.FaskesService

class FaskesRepository(private val faskesService: FaskesService) {
    suspend fun getAllFaskes(): List<FaskesModel> {
        val response = faskesService.getAllFaskes()
        var listFaskes: List<FaskesModel> = emptyList()
        if(response.isSuccessful){
            response.body()?.let {
                listFaskes = it
            }
        }
        return listFaskes
    }
}