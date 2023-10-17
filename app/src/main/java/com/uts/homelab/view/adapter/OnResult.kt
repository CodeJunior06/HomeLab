package com.uts.homelab.view.adapter

import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.State

interface OnResult {
    fun onResponse(appointmentModel: AppointmentUserModel, state:State?)
}