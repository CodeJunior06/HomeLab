package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainModel @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    suspend fun setEmailAndPasswordByCreate(email: String, password: String): ManagerError {
        return runCatching {
            firebaseRepository.isSetAuthentication(email, password)
        }.fold(
            onSuccess = {if(it.user !=null) ManagerError.Success(it.user!!) else ManagerError.Error("Error: Service Response is Failure") },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun setUserFirestore(
        valueRegister: Array<String>,
        firebaseUser: FirebaseUser
    ): ManagerError {
        return runCatching {
            firebaseRepository.setRegisterToFirestore(
                UserRegister(
                    valueRegister[0],
                    valueRegister[1],
                    valueRegister[2],
                    firebaseUser.email!!,
                    firebaseUser.uid
                )
            ).await()
        }.fold(
            onSuccess = { ManagerError.Success(0) },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    fun closeSession() {
        firebaseRepository.closeSession()
    }
}