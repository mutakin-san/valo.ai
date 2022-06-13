package com.capstone.valoai.features.maps.data

import android.util.Log
import com.capstone.valoai.commons.RecommendationService
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.maps.data.remote.FaskesService
import okhttp3.internal.filterList

class FaskesRepository(
    private val faskesService: FaskesService,
    private val recommendationService: RecommendationService
) {
    suspend fun getAllFaskes(riwayatVaksin: String): List<FaskesModel> {
        val response = faskesService.getAllFaskes()
        val recommendationVaccineType =
            recommendationService.getRecommendationVaccineType(riwayatVaksin)
        val listFaskes: ArrayList<FaskesModel> = arrayListOf()

        if (response.isSuccessful) {
            response.body()?.let {
                it.forEach { data ->
                    val vaccineTypes = mapOf(
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
        Log.d("HASIL", "getAllFaskes: $listFaskes")

        if (recommendationVaccineType.isSuccessful){
            recommendationVaccineType.body()?.let { vaccineTypes ->
                Log.d("REkomendasi", "Rekomendasi ${vaccineTypes.recommendations}")
                val newListFaskes = listFaskes.filter {it.availableVaccineType.containsAll(vaccineTypes.recommendations) }
        Log.d("HASIL", "new list: $newListFaskes")
                listFaskes.clear()
                listFaskes.addAll(newListFaskes)
            }
        }

        Log.d("HASIL", "getAllFaskes: $listFaskes")


        return listFaskes.toList()
    }
}