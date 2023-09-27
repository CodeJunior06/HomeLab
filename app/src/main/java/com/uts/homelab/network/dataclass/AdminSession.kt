package com.uts.homelab.network.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adminSession")
data class AdminSession (
    @PrimaryKey
    val id: String = "",
    val name:String = "",
    val email:String = "",
    val phone:String = "",
    var ip:String = "",
    var lastDate:String = "",
    var lastHour:String = "",
    val rol:String =""
)