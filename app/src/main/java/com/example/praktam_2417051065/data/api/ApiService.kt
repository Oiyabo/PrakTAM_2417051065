package com.example.praktam_2417051065.data.api

import com.example.praktam_2417051065.data.model.DataClusterResponse
import retrofit2.http.GET

interface ApiService {
    // Menggunakan path relatif terhadap BASE_URL di RetrofitClient
    @GET(".")
    suspend fun getDataCluster(): DataClusterResponse
}
