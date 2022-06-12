package com.capstone.valoai.features.profile.domain.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.valoai.commons.Resource
import com.capstone.valoai.features.profile.data.models.Profile
import com.capstone.valoai.features.profile.data.remote.UserDataSourceRemote
import kotlinx.coroutines.Dispatchers

class ProfileViewModel(private val datasource: UserDataSourceRemote) : ViewModel() {
    fun getProfile() = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = datasource.getProfile()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun putProfile(profile: Profile) = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = datasource.putProfile(profile)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}