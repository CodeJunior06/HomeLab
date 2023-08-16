package com.uts.homelab.network.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.uts.homelab.network.dataclass.Geolocation

class Converters {

    @TypeConverter
    fun toModel(modelo: Geolocation?): String? {
        return Gson().toJson(modelo)
    }

    @TypeConverter
    fun fromModel(modelo: String?): Geolocation? {
        return Gson().fromJson(modelo, Geolocation::class.java)
    }
}