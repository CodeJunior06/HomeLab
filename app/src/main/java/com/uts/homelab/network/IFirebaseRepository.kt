package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.QuerySnapshot
import com.uts.homelab.network.dataclass.UserRegister

interface IFirebaseRepository {
    suspend fun isSetAuthentication(email:String,password:String): AuthResult
    suspend fun setRegisterToFirestore(model:UserRegister) : Task<Void>

    fun closeSession()
     suspend fun isUserAuth(email: String, password: String): AuthResult
    suspend fun isUserAdminFirestore(email: Any) : QuerySnapshot
    suspend fun isUserNurseFirestore(email: Any) :QuerySnapshot
    suspend fun isUserPatientFirestore(email: Any) :QuerySnapshot


}