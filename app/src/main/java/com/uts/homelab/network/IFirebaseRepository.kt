package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.uts.homelab.network.dataclass.UserRegister

interface IFirebaseRepository {
    suspend fun isSetAuthentication(email:String,password:String): AuthResult
    suspend fun setRegisterToFirestore(model:UserRegister) : Task<Void>

    fun closeSession()


}