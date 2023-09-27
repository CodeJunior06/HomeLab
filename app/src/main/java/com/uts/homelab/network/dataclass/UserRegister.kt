package com.uts.homelab.network.dataclass

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "userSession")
class UserRegister() : Parcelable{
    var name: String =""
    var lastName : String = ""
    var typeDocument: String =""
    var valueDocument: String = ""
    var email: String =""
    @PrimaryKey(autoGenerate = false)
    var uid: String =""
    var phone: Long = 0
    var age: Int = 0
    var gender: String = ""
    var eps: String = ""
    var nacimiento: Long = 0
     var address: String = ""
    var geolocation: Geolocation = Geolocation()
    var newUser: Boolean = true
    var rol:String = ""
}

