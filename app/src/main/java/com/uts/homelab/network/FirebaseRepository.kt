package com.uts.homelab.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.UserRegister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IFirebaseRepository {

    override suspend fun isSetAuthentication(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
        }
    }

    override suspend fun isSetAuthenticationToken(token: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.signInWithCustomToken(token).await()
        }
    }

    override suspend fun getToken(): String {
        return withContext(Dispatchers.IO) {
            auth.currentUser!!.getIdToken(true).await().token!!.trim()
        }
    }

    override suspend fun setRegisterUserToFirestore(model: UserRegister): Task<Void> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Users").document().set(model)
        }
    }

    override suspend fun setRegisterNurseToFirestore(model: NurseRegister): Task<Void> {
        return withContext(Dispatchers.IO) {
            firestore.collection("Nurses").document().set(model)
        }
    }

    override suspend fun isAuth(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
        }
    }

    override suspend fun isUserAdminFirestore(email: Any): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Admins").whereEqualTo("email", email).get().await()
        }
    }

    override suspend fun isUserNurseFirestore(email: Any): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Nurses").whereEqualTo("email", email).get().await()
        }
    }

    override suspend fun isUserPatientFirestore(email: Any): QuerySnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("Users").whereEqualTo("email", email).get().await()
        }
    }

    override fun closeSession() {
        auth.signOut()
    }
}