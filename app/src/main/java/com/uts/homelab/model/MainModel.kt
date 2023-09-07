package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.network.db.entity.AdminSession
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome,
    private val dataStore: DataStoreManager
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

            val modelUser = UserRegister()
            modelUser.name = valueRegister[0]
            modelUser.lastName = valueRegister[1]
            modelUser.email = firebaseUser.email!!
            modelUser.uid = firebaseUser.uid

            firebaseRepository.setRegisterUserToFirestore(
                modelUser
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
                dataStore.setStringDataStore(
                    DataStoreManager.PREF_USER_AUTH,
                    DataStoreManager.passAuth,
                    password
                )
                ManagerError.Success(it.user!!.email!!)
            },
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
                val response = resUser.toObjects(UserRegister::class.java)[0]
                roomRepository.userSessionDao()
                    .insertNurseSession(response)
                ManagerError.Success(1)
            } else if (!resNurse.isEmpty) {
                roomRepository.nurseSessionDao()
                    .insertNurseSession(resNurse.toObjects(NurseRegister::class.java)[0])
                ManagerError.Success(2)
            } else {
                roomRepository.adminSessionDao().insertAdmin(querySnapshot(resAdmin))
                ManagerError.Success(3)
            }
        }.onFailure {
            ManagerError.Error(it.message!!)
        }
    }

    private fun querySnapshot(document: QuerySnapshot): AdminSession {
        return AdminSession(
            document.documents[0].get("id").toString(),
            document.documents[0].get("name").toString(),
            document.documents[0].get("email").toString()
        )
    }

    suspend fun isTrueSetNewInstall(bool: Boolean) {
        dataStore.setBoolDataStore(
            DataStoreManager.PREF_APP_INFO,
            DataStoreManager.isNewInstall,
            bool
        )
    }


    fun isGetNewInstall(): Flow<Boolean?> {
        return dataStore.getBoolDataStore(
            DataStoreManager.PREF_APP_INFO,
            DataStoreManager.isNewInstall
        )
    }
}