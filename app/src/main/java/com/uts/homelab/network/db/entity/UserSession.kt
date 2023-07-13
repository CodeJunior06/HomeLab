package com.uts.homelab.network.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userSession")
data class UserSession (
    @PrimaryKey
    val id: String,
    val name:String,
    val email:String
)