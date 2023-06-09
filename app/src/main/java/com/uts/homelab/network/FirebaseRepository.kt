package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uts.homelab.network.dataclass.UserRegister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val auth:FirebaseAuth,private val firestore:FirebaseFirestore) : IFirebaseRepository{
    override suspend fun isSetAuthentication(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO){
            auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
        }
    }

    override suspend fun setRegisterToFirestore(model: UserRegister): Task<Void> {
        return withContext(Dispatchers.IO){
           firestore.collection("Users").document().set(model)
        }
    }

    override  fun closeSession() {
        auth.signOut()
    }


}