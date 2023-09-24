package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.NurseLocation
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdminModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val room: DataBaseHome,
    private val dataStore: DataStoreManager
) {

    suspend fun getModelData(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                room.adminSessionDao().getAdminAuth()
            }.fold(
                onSuccess = {
                    ManagerError.Success(it)
                },
                onFailure = { ManagerError.Error(Utils.messageErrorConverter(Cons.ROOM_GET_ERROR)) }
            )
        }
    }

    suspend fun deleteModelData(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                room.adminSessionDao().deleteAdminAuth(firebaseRepository.getAuth().uid!!)
            }.fold(
                onSuccess = {
                    dataStore.setStringDataStore(
                        DataStoreManager.PREF_USER_AUTH,
                        DataStoreManager.passAuth,
                        ""
                    )
                    ManagerError.Success(it) },
                onFailure = { ManagerError.Error(Utils.messageErrorConverter(Cons.ROOM_DELETE_ERROR))}
            )
        }
    }

    suspend fun setRegisterNurse(email: String, password: String): ManagerError {

        return kotlin.runCatching {
                firebaseRepository.isSetAuthentication(email, password)
            }.fold(
                onSuccess = {
                    if (it.user != null) {
                        ManagerError.Success(it.user!!)
                    } else ManagerError.Error(
                        Utils.messageErrorConverter(-200)
                    )
                },
                onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
            )
    }

    suspend fun setNurseFirestore(
        valueRegister: Array<String?>,
        firebaseUser: FirebaseUser
    ): ManagerError {

        return withContext(Dispatchers.IO) {
            runCatching {

                firebaseRepository.closeSession()
                firebaseRepository.isAuth(room.adminSessionDao().getAdminAuth().email,dataStore.getStringDataStore(DataStoreManager.PREF_USER_AUTH,DataStoreManager.passAuth).first().toString())

                val nurse = NurseRegister()
                nurse.name = valueRegister[0]
                nurse.lastName = valueRegister[1]
                nurse.email = firebaseUser.email!!
                nurse.valueDocument = valueRegister[3]
                nurse.gender = valueRegister[4]
                nurse.uid = firebaseUser.uid

                firebaseRepository.setRegisterNurseToFirestore(nurse).await()
            }.fold(
                onSuccess = { ManagerError.Success(1) },
                onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
            )
        }
    }

    suspend fun getWorkingDayAvailable(): ManagerError {

        return kotlin.runCatching {
            firebaseRepository.getNursesByJournal()
        }.fold(
            onSuccess = {
                val modelWorking = it.toObjects(WorkingDayNurse::class.java).toList()
                getNurseAvailableByListIds(modelWorking)
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }

    private suspend fun getNurseAvailableByListIds(modelWorking: List<WorkingDayNurse>): ManagerError{

        return kotlin.runCatching {
            val lstUid = ArrayList<String>()
            modelWorking.forEach{
                lstUid.add(it.id)
            }

            firebaseRepository.getIdsNursesAvailable(lstUid)
        }.fold(
            onSuccess = {
                val modelNurse = it.toObjects(NurseRegister::class.java).toList()
                val lstNurseLocation = ArrayList<NurseLocation>()
                modelNurse.forEachIndexed { index, nurseRegister ->
                    if(modelWorking[index].id == nurseRegister.uid){
                        val nurseLocation  = NurseLocation()
                        nurseLocation.geolocation = modelWorking[index].geolocation
                        nurseLocation.phone = ""
                        nurseLocation.uidWorking = modelWorking[index].id
                        nurseLocation.nameUser = nurseRegister.name!!
                        nurseLocation.lastName  = nurseRegister.lastName!!
                        lstNurseLocation.add(nurseLocation)
                    }
                }

                ManagerError.Success(lstNurseLocation)
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!))}
        )
    }

      fun getNursesChangeWorkingDay(onCall: (WorkingDayNurse) -> Unit) {
        try {
            firebaseRepository.realTimeWorkingDayAllCollection(onCall)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}