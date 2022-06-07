package com.capstone.valoai.features.maps.domain.usecase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.valoai.features.maps.data.FaskesRepository

class ViewModelFactory(private val faskesRepository: FaskesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FaskesViewModel::class.java)) {
            return FaskesViewModel(faskesRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}