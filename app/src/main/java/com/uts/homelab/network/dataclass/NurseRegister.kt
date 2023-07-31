package com.uts.homelab.network.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "nurseSession")
class NurseRegister {
    var name: String? = ""
    var lastName: String? = ""
    var email: String? = ""
    var valueDocument: String? = ""
    var gender: String? = ""
    @PrimaryKey(autoGenerate = false)
    var uid: String = ""
    var address: String? = ""
    var geolocation: Geolocation = Geolocation()
    var exp: Int = 0
    var age: Int = 0
    var isAutomobile: Boolean = false
    var isNew = true
}