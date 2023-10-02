package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.*
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerAppointmentUserModel
import com.uts.homelab.utils.response.ManagerCommentType
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

    suspend fun updateModelDataFirestore(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {

                val resRoom = room.adminSessionDao().getAdminAuth()
                resRoom.ip = Utils.getIPAddress()

                room.adminSessionDao().updateAdmin(resRoom)
                firebaseRepository.updateAdmin(resRoom)
            }.fold(
                onSuccess = {
                    ManagerError.Success(it)
                },
                onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
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
                nurse.name = valueRegister[0]!!
                nurse.lastName = valueRegister[1]!!
                nurse.email = firebaseUser.email!!
                nurse.valueDocument = valueRegister[3]!!
                nurse.gender = valueRegister[4]!!
                nurse.uid = firebaseUser.uid
                nurse.rol = Rol.NURSE.name

                firebaseRepository.setRegisterNurseToFirestore(nurse).await()
            }.fold(
                onSuccess = { ManagerError.Success(1) },
                onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
            )
        }
    }

    suspend fun getWorkingDayAvailable(): ManagerError {

        return kotlin.runCatching {
            firebaseRepository.getNursesActiveByJournal()
        }.fold(
            onSuccess = {
                val res= it
                println(res)
                val modelWorking = it.toObjects(WorkingDayNurse::class.java).toList()
                getNurseAvailableByListIds(modelWorking)
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }

     suspend fun getNurseAvailableByListIds(modelWorking: List<WorkingDayNurse>): ManagerError{

        return kotlin.runCatching {
            val lstUid = ArrayList<String>()
            modelWorking.forEach{
                lstUid.add(it.id)
            }

            firebaseRepository.getIdsNursesAvailable(lstUid)
        }.fold(
            onSuccess = {
                val lstModelNurse = it.toObjects(NurseRegister::class.java).toList()

                    val lstNurseLocation = ArrayList<NurseLocation>()
                    lstModelNurse.forEach {nurseRegister ->
                        modelWorking.forEach {
                        if(it.id == nurseRegister.uid){
                            val nurseLocation  = NurseLocation()
                            nurseLocation.geolocation = it.geolocation
                            nurseLocation.phone = ""
                            nurseLocation.uidWorking = it.id
                            nurseLocation.nameUser = nurseRegister.name
                            nurseLocation.lastName  = nurseRegister.lastName
                            lstNurseLocation.add(nurseLocation)
                        }
                    }
            }
                    ManagerError.Success(lstNurseLocation)

            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!))}
        )
    }
    suspend fun getAllWorkingDay(): ManagerError {

        return kotlin.runCatching {
            firebaseRepository.getAllNursesByJournal()
        }.fold(
            onSuccess = {
                val lstModelWorking = it.toObjects(WorkingDayNurse::class.java).toList()
                getAllNurses(lstModelWorking)

            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }

    private suspend fun getAllNurses(lstWorkingDay:List<WorkingDayNurse>): ManagerError {

        return kotlin.runCatching {
            firebaseRepository.getAllNurses()
        }.fold(
            onSuccess = {
              val lstNurses = it.toObjects(NurseRegister::class.java).toList()
                val lstNurseWorkingAdapter = ArrayList<NurseWorkingAdapter>()
                lstNurses.forEach {  nurse->
                    lstWorkingDay.forEach{
                        if(nurse.uid == it.id){
                            val model = NurseWorkingAdapter()
                            model.uidNurse = it.id
                            model.active = it.active
                            model.name = nurse.name
                            model.lastName = nurse.lastName
                            model.gender = nurse.gender
                            model.geolocation = it.geolocation
                            model.idMotorcycle = nurse.idVehicle
                            model.phone = nurse.phone
                            lstNurseWorkingAdapter.add(model)
                        }
                    }
                }
                ManagerError.Success(lstNurseWorkingAdapter)
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )
    }
      fun getNursesChangeWorkingDay(onCall: (WorkingDayNurse) -> Unit) {
        try {
            firebaseRepository.realTimeWorkingDayAllCollection(onCall)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    suspend fun getAllOpinionPQRS(): ManagerCommentType {


            return kotlin.runCatching {
                firebaseRepository.getAllTypeComment()
            }.fold(
                onSuccess = {
                    val lstModelWorking = it.toObjects(CommentType::class.java).toList()
                   ManagerCommentType.Success(lstModelWorking)

                },
                onFailure = { ManagerCommentType.Error(Utils.messageErrorConverter(it.message!!)) }
            )

    }

    suspend fun getAppointmentLaboratory(): ManagerAppointmentUserModel {

        return kotlin.runCatching {
            firebaseRepository.getAppointmentStateLaboratory()
        }.fold(
            onSuccess = {
                val lstModelWorking = it.toObjects(AppointmentUserModel::class.java).toList()
                ManagerAppointmentUserModel.Success(lstModelWorking)
            },
            onFailure = { ManagerAppointmentUserModel.Error(Utils.messageErrorConverter(it.message!!)) }
        )

    }

    suspend fun setDateTimeLastConnection(): ManagerError {

        return kotlin.runCatching {

            val resAdmin = room.adminSessionDao().getAdminAuth()
            resAdmin.lastDate = Utils().getCurrentDate()
            resAdmin.lastHour = Utils.getCurrentTime()

            firebaseRepository.updateAdmin(resAdmin)
        }.fold(
            onSuccess = {
               ManagerError.Success(1)
            },
            onFailure = { ManagerError.Error(Utils.messageErrorConverter(it.message!!)) }
        )

    }

    fun stopAsync() {
        try {
            firebaseRepository.stopAsyncWorkingDay()
        }catch (e:Exception){
            println(e)
        }
    }

}