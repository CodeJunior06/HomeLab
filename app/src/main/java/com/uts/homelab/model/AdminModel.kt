package com.uts.homelab.model

import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.AuthSingleton
import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdminModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val room: DataBaseHome,
    private val dataStore: DataStoreManager
) {

    suspend fun getModelData(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                room.adminSessionDao().getAdminAuth()
            }.fold(
                onSuccess = {
                    AuthSingleton.getInstance().uid= it.id
                    ManagerError.Success(it)
                },
                onFailure = { ManagerError.Error(it.message!!) }
            )
        }
    }

    suspend fun deleteModelData(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                room.adminSessionDao().deleteAdminAuth(firebaseRepository.getAuth().uid!!)
                AuthSingleton.getInstance().uid = ""

            }.fold(
                onSuccess = {
                    dataStore.setStringDataStore(
                        DataStoreManager.PREF_USER_AUTH,
                        DataStoreManager.passAuth,
                        ""
                    )
                    ManagerError.Success(it) },
                onFailure = { ManagerError.Error(it.message!!) }
            )
        }
    }

    suspend fun setRegisterNurse(email: String, password: String): ManagerError {

        return kotlin.runCatching {
                firebaseRepository.isSetAuthentication(email, password)
            }.fold(
                onSuccess = {
                    if (it.user != null) {
                        ManagerError.Success(it.user!!)
                    } else ManagerError.Error(
                        "Error: Service Response is Failure"
                    )
                },
                onFailure = { ManagerError.Error(it.message!!) }
            )
    }

    suspend fun setNurseFirestore(
        valueRegister: Array<String?>,
        firebaseUser: FirebaseUser
    ): ManagerError {

        return withContext(Dispatchers.IO) {
            runCatching {

                firebaseRepository.closeSession()
                firebaseRepository.isAuth(room.adminSessionDao().getAdminAuth().email,dataStore.getStringDataStore(DataStoreManager.PREF_USER_AUTH,DataStoreManager.passAuth).first().toString())

                val nurse = NurseRegister()
                nurse.name = valueRegister[0]
                nurse.lastName = valueRegister[1]
                nurse.email = firebaseUser.email!!
                nurse.valueDocument = valueRegister[3]
                nurse.gender = valueRegister[4]
                nurse.uid = firebaseUser.uid

                firebaseRepository.setRegisterNurseToFirestore(nurse).await()
            }.fold(
                onSuccess = { ManagerError.Success(1) },
                onFailure = { ManagerError.Error(it.message!!) }
            )
        }
    }
}