package com.uts.homelab.network.dataclass

data class ResultAppointment(
    var description:String = "",
    var result:String = "",
    var tsResult:String = "",
    val appointmentUserModel: AppointmentUserModel
)
