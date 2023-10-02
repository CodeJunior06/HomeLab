package com.uts.homelab.network.dataclass

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "nurseSession")
class NurseRegister : Parcelable {
    var name: String = ""
    var lastName: String = ""
    var email: String = ""
    var valueDocument: String = ""
    var gender: String = ""
    var nacimiento:String = ""
    @PrimaryKey(autoGenerate = false)
    var uid: String = ""
    var address: String = ""
    var geolocation: Geolocation = Geolocation()
    var exp: Int = 0
    var age: Int = 0
    var newNurse = true
    var idVehicle:String = ""
    var phone = ""
    var rol = ""
}