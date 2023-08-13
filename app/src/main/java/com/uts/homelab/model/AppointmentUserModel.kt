package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.AuthSingleton
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.AppoimentUserModel
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppointmentUserModel @Inject constructor(private val firebaseRepository: FirebaseRepository,
                                               private val roomRepository: DataBaseHome) {

    suspend fun setAppointmentUserFirestore(
        valueAppointment: Array<String>
    ): ManagerError {
        return runCatching {
            firebaseRepository.setAppointmentToFirestore(
                AppoimentUserModel(
                    valueAppointment[0],
                    valueAppointment[1],
                    valueAppointment[2],
                    valueAppointment[3],
                    valueAppointment[4]
                )
            ).await()
        }.fold(
            onSuccess = { ManagerError.Success(0) },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }
}