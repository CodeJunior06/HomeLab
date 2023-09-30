package com.uts.homelab.network.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class AppointmentUserModel : Parcelable {
    var eps: String = ""
    var phone: Long = 0
    var address: String = ""
    var geolocation: Geolocation = Geolocation()
    var uidUser: String = ""
    var typeOfExam: String = ""
    var date: String = ""
    var hour: String = ""
    var uidNurse: String = ""
    var state: String = ""
    var idResult: String = ""
    var step:Int = 0
    var modelNurse:NurseRegister = NurseRegister()
    var modelUser:UserRegister = UserRegister()
}
