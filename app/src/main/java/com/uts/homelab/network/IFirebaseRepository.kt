package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.uts.homelab.network.dataclass.Job
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse

interface IFirebaseRepository {
    suspend fun isSetAuthentication(email: String, password: String): AuthResult
    suspend fun isSetAuthenticationToken(token: String): AuthResult

    suspend fun isAuth(email: String, password: String): AuthResult
    suspend fun setRegisterUserToFirestore(model: UserRegister): Task<Void>
    suspend fun setRegisterNurseToFirestore(model: NurseRegister): Task<Void>

    suspend fun setRegisterWorkingNurse(model: WorkingDayNurse) : Task<*>
    suspend fun setRegisterAvailableAppointment(modelJob: Job) : Task<*>
    suspend fun updateNurseFirestore(map: Map<String,Any>): Task<*>
    suspend fun updateUserFirestore(map: Map<String,Any>): Task<*>

    suspend fun updateAvailableFirestore(job: Job, uidNurse: String): Task<*>

    suspend fun getToken(): String

    suspend fun setAppointmentToFirestore(appointmentUserModel: AppointmentUserModel): Task<Void>
    suspend fun isUserAdminFirestore(email: Any): QuerySnapshot
    suspend fun isUserNurseFirestore(email: Any): QuerySnapshot
    suspend fun isUserPatientFirestore(email: Any): QuerySnapshot
    suspend fun getNurseAvailable(): QuerySnapshot
    suspend fun getIdNurseAvailable(uid:String): DocumentSnapshot

    suspend fun getIdsNursesAvailable(list:ArrayList<String>): QuerySnapshot

    suspend fun getAppointmentByDate(date:String,typeUser:String) : QuerySnapshot
    suspend fun getAppointmentAllByUser() : QuerySnapshot


    fun closeSession()
    suspend fun setTypeComment(model: Map<String, String?>) : Task<*>
    suspend fun requestChangePassword(email: String)
    suspend fun updateDataUserFirestore(register: UserRegister) : Task<*>
    suspend fun getJournal() : QuerySnapshot
    suspend fun updateJournal(workingDayNurse: WorkingDayNurse, idDoc: String) : Task<*>

    suspend fun getNursesByJournal() : QuerySnapshot
}