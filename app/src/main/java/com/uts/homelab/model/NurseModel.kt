package com.uts.homelab.model

import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.Geolocation
import com.uts.homelab.network.dataclass.Job
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NurseModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome,
    private val dataStore: DataStoreManager
){
    suspend fun initView() : NurseRegister {
       return roomRepository.nurseSessionDao().getUserAuth()
    }

    suspend fun setRegisterNurse(nurseData: Array<String?>, value: NurseRegister) : ManagerError {
        val map = HashMap<String, Any>()

        value.age = nurseData[0]!!.toInt()
        value.exp = nurseData[1]!!.toInt()
        value.address = nurseData[2]!!
        value.idVehicle = nurseData[3]!!
        value.geolocation.latitude = nurseData[4]!!
        value.geolocation.longitude = nurseData[5]!!
        value.newNurse = false

        map["geolocation"] = value.geolocation
        map["age"] = value.age
        map["exp"] = value.exp
        map["address"] = value.address
        map["idVehicle"] = value.idVehicle
        map["newNurse"] = value.newNurse

        return kotlin.runCatching {
            firebaseRepository.updateNurseFirestore(map).await()
        }.fold(
            onSuccess = {
                roomRepository.nurseSessionDao().updateNurseSession(value)
                insertJournal(value.geolocation).getOrThrow()
            }, onFailure = {
                ManagerError.Error(it.message!!)
            }
        )

    }

    suspend fun deleteSessionRoom() {
        roomRepository.nurseSessionDao().deleteNurseSession(roomRepository.nurseSessionDao().getUserAuth())
    }

    private suspend fun insertJournal(geo: Geolocation): Result<ManagerError>{

        val modelWorking = WorkingDayNurse(geo)

        return kotlin.runCatching {
                firebaseRepository.setRegisterWorkingNurse(modelWorking)
                firebaseRepository.setRegisterAvailableAppointment(Job().apply { init() })
            ManagerError.Success(0)
        }.   onFailure {
            it.printStackTrace()
            ManagerError.Error(it.message!!)
        }
    }
}