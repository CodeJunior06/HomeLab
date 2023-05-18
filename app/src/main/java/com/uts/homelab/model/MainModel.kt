package com.uts.homelab.model

import com.uts.homelab.network.FirebaseRepository
import javax.inject.Inject

class MainModel @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    suspend fun setEmailAndPasswordByCreate(email: String, password: String) {
       val response =  firebaseRepository.isSetAuthentication(email, password)
        if (response.isSuccessful){
            response.result.user
        }
    }
}