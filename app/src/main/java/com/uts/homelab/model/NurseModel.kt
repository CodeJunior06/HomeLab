package com.uts.homelab.model

import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.*
import com.uts.homelab.network.db.Constants
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NurseModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome,
    private val dataStore: DataStoreManager
) {
    suspend fun initView(): NurseRegister {
        return roomRepository.nurseSessionDao().getUserAuth()
    }

    suspend fun setRegisterNurse(nurseData: Array<String?>, value: NurseRegister): ManagerError {
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
        roomRepository.nurseSessionDao()
            .deleteNurseSession(roomRepository.nurseSessionDao().getUserAuth())
        roomRepository.nurseSessionDao().deleteWorkingDay(roomRepository.nurseSessionDao().getNurseWorkingDay())
    }

    private suspend fun insertJournal(geo: Geolocation): Result<ManagerError> {

        val modelWorking = WorkingDayNurse()

        modelWorking.geolocation = geo

        return kotlin.runCatching {
            firebaseRepository.setRegisterWorkingNurse(modelWorking)
            firebaseRepository.setRegisterAvailableAppointment(Job().apply { init(firebaseRepository.getAuth().uid!!) })
            ManagerError.Success(0)
        }.onFailure {
            it.printStackTrace()
            ManagerError.Error(it.message!!)
        }
    }

    private var idDoc = ""
    suspend fun getJournalByNurse(): ManagerError {
        return kotlin.runCatching {
            firebaseRepository.getJournal()
        }.fold(
            onSuccess = {
                val model = it.documents[0].toObject(WorkingDayNurse::class.java)!!
                idDoc = it.documents[0].id
                roomRepository.nurseSessionDao().insertNurseWorkingDay(model)
                ManagerError.Success(model)
            }, onFailure = {
                ManagerError.Error(it.message!!)
            }
        )

    }

    suspend fun changeJournalByNurse(value: WorkingDayNurse, bool: Boolean): ManagerError {

        value.active = bool
        return kotlin.runCatching {
            firebaseRepository.updateJournal(value, idDoc)
        }.fold(
            onSuccess = {
                ManagerError.Success("1")
            }, onFailure = {
                ManagerError.Error(it.message!!)
            }
        )

    }

    suspend fun getAppointment(): ManagerError {
        return runCatching {
            firebaseRepository.getAppointmentByDate(Utils().getCurrentDate(),Constants.APPOINTMENT_UID_NURSE)
        }.fold(
            onSuccess = {
                val res = it.toObjects(AppointmentUserModel::class.java).toList()
                ManagerError.Success(res)
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

}