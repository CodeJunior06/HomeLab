package com.uts.homelab.network.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workingDay")
 class WorkingDayNurse{
    var geolocation: Geolocation = Geolocation()
    var active:Boolean = false
    @PrimaryKey
    var id:String = ""
}
