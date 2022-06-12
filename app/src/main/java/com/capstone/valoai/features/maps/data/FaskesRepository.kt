package com.capstone.valoai.features.maps.data

import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.maps.data.remote.FaskesService

class FaskesRepository(private val faskesService: FaskesService) {
    suspend fun getAllFaskes(): List<FaskesModel> {
        val response = faskesService.getAllFaskes()
        val listFaskes: ArrayList<FaskesModel> = arrayListOf()
        if (response.isSuccessful) {
            response.body()?.let {
                it.forEach { data ->

                    val vaccineTypes = mapOf<String, Int>(
                        "astrazaneca" to data.astrazaneca.toInt(),
                        "janssen" to data.janssen.toInt(),
                        "moderna" to data.moderna.toInt(),
                        "pfizer" to data.pfizer.toInt(),
                        "sinopharm" to data.sinopharm.toInt(),
                        "sinovac" to data.sinovac.toInt()
                    )

                    val item = FaskesModel(
                        data.id.toInt(),
                        data.name,
                        data.latitude,
                        data.longitude,
                        availableVaccineType = vaccineTypes.filterValues { stock -> stock > 0 }
                            .keys.toList(),
                        description = "Tidak ada deskripsi"
                    )

                    listFaskes.add(item)
                }
            }
        }
        return listFaskes.toList()
    }
}