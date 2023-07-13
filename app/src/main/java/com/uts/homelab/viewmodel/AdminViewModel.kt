package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.AuthSingleton
import com.uts.homelab.model.AdminModel
import com.uts.homelab.network.db.entity.UserSession
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(private val adminModel:AdminModel) : ViewModel() {

    val isUserAuth = MutableLiveData<UserSession>()
    val intentToLogin = MutableLiveData<Unit>()

    fun getTextUI(){
        viewModelScope.launch {
           when (val res = adminModel.getModelData()){
               is ManagerError.Error ->{

               }
               is ManagerError.Success ->{
                    isUserAuth.postValue(res.modelSuccess as UserSession)
               }
               else -> {}
           }
        }
    }

    fun deleteUserSession(){
        viewModelScope.launch {
            when (val res = adminModel.deleteModelData()){
                is ManagerError.Error ->{

                }
                is ManagerError.Success ->{
                    intentToLogin.postValue(res.modelSuccess as Unit)
                }
                else -> {}
            }
        }
    }
}