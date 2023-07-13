package com.uts.homelab.model

import com.uts.homelab.AuthSingleton
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.response.ManagerError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdminModel @Inject constructor(private val room: DataBaseHome) {

    suspend fun getModelData(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                room.userSessionDao().getUserAuth()
            }.fold(
                onSuccess = {
                    AuthSingleton().uid = it.id
                    ManagerError.Success(it) },
                onFailure = { ManagerError.Error(it.message!!) }
            )
        }
    }
    suspend fun deleteModelData(): ManagerError {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                room.userSessionDao().deleteUserAuth(AuthSingleton().uid)
            }.fold(
                onSuccess = { ManagerError.Success(it) },
                onFailure = { ManagerError.Error(it.message!!) }
            )
        }
    }
}