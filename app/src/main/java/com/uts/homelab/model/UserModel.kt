package com.uts.homelab.model

import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.AppoimentUserModel
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome
) {

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

    suspend fun initView(): UserRegister {
        return roomRepository.userSessionDao().getUserAuth()
    }

    suspend fun closeSession(): Boolean {
        return try {
            roomRepository.userSessionDao()
                .deleteUserSession(roomRepository.userSessionDao().getUserAuth())
            firebaseRepository.closeSession()
            true
        } catch (e: Exception) {
            false
        }

    }

    suspend fun saveUserRoom(value: UserRegister) {
        roomRepository.userSessionDao().updateUserSession(value)
    }

    suspend fun saveUserFirestore(arrayOf: Array<String?>, modelUser: UserRegister?): ManagerError {
        val userRegister = modelUser ?: roomRepository.userSessionDao().getUserAuth()
        val map = HashMap<String, Any>()

        userRegister.geolocation.longitude = arrayOf[2]
        userRegister.geolocation.latitude = arrayOf[1]
        userRegister.address = arrayOf[0]!!

        map["geolocation"] = userRegister.geolocation
        map["age"] = userRegister.age
        map["address"] = userRegister.address
        map["eps"] = userRegister.eps
        map["gender"] = userRegister.gender
        map["newUser"] = false
        map["nacimiento"] = userRegister.nacimiento
        map["phone"] = userRegister.phone
        map["typeDocument"] = userRegister.typeDocument
        map["valueDocument"] = userRegister.valueDocument


        return kotlin.runCatching {
            firebaseRepository.updateUserFirestore(map).await()
        }.fold(
            onSuccess = {
                userRegister.newUser = false
                roomRepository.userSessionDao().updateUserSession(userRegister)
                ManagerError.Success("0")
            }, onFailure = {
                ManagerError.Error(it.message!!)
            }
        )

    }

    suspend fun getNurseAvailable(): ManagerError {
        return runCatching {
          firebaseRepository.getAuth()
        }.fold(
            onSuccess = { ManagerError.Success(0) },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }
}