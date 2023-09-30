package com.uts.homelab.view.adapter

import com.uts.homelab.network.dataclass.AppointmentUserModel

interface OnResult {

    fun onSuccess(appointmentModel: AppointmentUserModel)
    fun onCancel()
}