package com.capstone.valoai.features.maps.domain.usecase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.valoai.commons.Resource
import com.capstone.valoai.features.maps.data.FaskesRepository
import kotlinx.coroutines.Dispatchers

class FaskesViewModel(private val faskesRepository: FaskesRepository) : ViewModel() {
    fun getAllFaskes(riwayatVaksin: String) = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = faskesRepository.getAllFaskes(riwayatVaksin)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}