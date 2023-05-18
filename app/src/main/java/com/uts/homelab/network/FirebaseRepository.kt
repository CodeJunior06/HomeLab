package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val auth:FirebaseAuth,private val firestore:FirebaseFirestore) : IFirebaseRepository{
    override suspend fun isSetAuthentication(email: String, password: String): Task<AuthResult> {
        return withContext(Dispatchers.IO){
            auth.createUserWithEmailAndPassword(email, password)
        }
    }


}