package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.QuerySnapshot
import com.uts.homelab.network.dataclass.Job
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.AppoimentUserModel
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
    suspend fun getToken(): String

    suspend fun setRegisterToFirestore(model: UserRegister): Task<Void>
    suspend fun setAppointmentToFirestore(appoimentUserModel: AppoimentUserModel): Task<Void>
    suspend fun isUserAuth(email: String, password: String): AuthResult

    suspend fun isUserAdminFirestore(email: Any): QuerySnapshot
    suspend fun isUserNurseFirestore(email: Any): QuerySnapshot
    suspend fun isUserPatientFirestore(email: Any): QuerySnapshot

    fun closeSession()
}