package com.capstone.valoai.commons

import com.capstone.valoai.BuildConfig
import com.capstone.valoai.features.maps.data.remote.FaskesService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getRetrofit(baseUrl : String): Retrofit {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val recommendationService: RecommendationService = getRetrofit("https://api-valo-model.herokuapp.com/").create(RecommendationService::class.java)
    val faskesService: FaskesService = getRetrofit("https://f1992759-5b0a-4678-9cda-d7356f29551c.mock.pstmn.io/").create(FaskesService::class.java)
}