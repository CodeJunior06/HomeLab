package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.network.db.entity.UserSession
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome,
    private val dataStore:DataStoreManager
) {
    suspend fun setEmailAndPasswordByCreate(email: String, password: String): ManagerError {
        return runCatching {
            firebaseRepository.isSetAuthentication(email, password)
        }.fold(
            onSuccess = {
                if (it.user != null) ManagerError.Success(it.user!!) else ManagerError.Error(
                    "Error: Service Response is Failure"
                )
            },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun setUserFirestore(
        valueRegister: Array<String>,
        firebaseUser: FirebaseUser
    ): ManagerError {
        return runCatching {
            firebaseRepository.setRegisterUserToFirestore(
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

    suspend fun getUserAuth(email: String, password: String): ManagerError {
        return runCatching {
            firebaseRepository.isAuth(
                email,
                password
            )

        }.fold(
            onSuccess = {
                dataStore.setStringDataStore(DataStoreManager.PREF_USER_AUTH,DataStoreManager.passAuth ,password)
                ManagerError.Success(it.user!!.email!!) },
            onFailure = { ManagerError.Error(it.message!!) }
        )
    }

    suspend fun setSession(email: Any): Result<ManagerError> {
        return runCatching {
            val reqAdmin = CoroutineScope(Dispatchers.IO).async {
                firebaseRepository.isUserAdminFirestore(
                    email
                )
            }
            val reqNurse = CoroutineScope(Dispatchers.IO).async {
                firebaseRepository.isUserNurseFirestore(
                    email
                )
            }
            val reqUser = CoroutineScope(Dispatchers.IO).async {
                firebaseRepository.isUserPatientFirestore(
                    email
                )
            }
            val resUser = reqUser.await()
            val resNurse = reqNurse.await()
            val resAdmin = reqAdmin.await()

            if (resUser.isEmpty && resNurse.isEmpty && resAdmin.isEmpty) {
                ManagerError.Error("User Not Register")
            }
            if (!resUser.isEmpty) {
                ManagerError.Success(1)
            } else if (!resNurse.isEmpty) {
                ManagerError.Success(2)
            } else {

                withContext(Dispatchers.IO) {
                    roomRepository.mainDao().insertUser(querySnapshot(resAdmin))
                }

                ManagerError.Success(3)
            }
        }.onFailure {
            ManagerError.Error(it.message!!)
        }
    }

    private fun querySnapshot(document: QuerySnapshot): UserSession {
        return UserSession(
            document.documents[0].get("id").toString(),
            document.documents[0].get("name").toString(),
            document.documents[0].get("email").toString()
        )
    }

    suspend fun isSetInstall(value: Boolean) {
        dataStore.setBoolDataStore(DataStoreManager.PREF_APP_INFO,DataStoreManager.isNewInstall,value)
    }
}