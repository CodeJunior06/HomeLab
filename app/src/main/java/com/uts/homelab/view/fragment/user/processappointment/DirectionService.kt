package com.uts.homelab.view.fragment.user.processappointment

import com.uts.homelab.network.dataclass.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionService {
    @GET("maps/api/directions/json")
    @JvmSuppressWildcards
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Call<DirectionsResponse>

}