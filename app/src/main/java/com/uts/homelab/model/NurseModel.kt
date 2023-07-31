package com.uts.homelab.model

import com.uts.homelab.network.FirebaseRepository
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.utils.datastore.DataStoreManager
import javax.inject.Inject

class NurseModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val roomRepository: DataBaseHome,
    private val dataStore: DataStoreManager
){
    suspend fun initView() : NurseRegister {
       return roomRepository.nurseSessionDao().getUserAuth()
    }
}