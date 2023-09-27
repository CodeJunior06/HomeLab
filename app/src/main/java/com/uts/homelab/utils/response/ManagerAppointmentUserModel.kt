package com.uts.homelab.utils.response

import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.CommentType

sealed class ManagerAppointmentUserModel{
    data class Success(val modelSuccess:List<AppointmentUserModel>) : ManagerAppointmentUserModel()
    data class Error(val error:String) : ManagerAppointmentUserModel()
}


