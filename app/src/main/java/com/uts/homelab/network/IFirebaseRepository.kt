package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface IFirebaseRepository {
    suspend fun isSetAuthentication(email:String,password:String): Task<AuthResult>


}