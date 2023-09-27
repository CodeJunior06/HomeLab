package com.uts.homelab.network.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adminSession")
data class AdminSession (
    @PrimaryKey
    val id: String,
    val name:String,
    val email:String,
    val phone:String = "",
    val ip:String = "",
    val lastDate:String = "",
    val lastHour:String = "",
    val rol:String =""
)