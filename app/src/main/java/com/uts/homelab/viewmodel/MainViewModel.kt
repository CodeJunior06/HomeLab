package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.model.MainModel
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainViewModel @Inject constructor(private val mainModel: MainModel) : ViewModel() {


    @Singleton
    var utils = Utils()
    val isErrorToast = MutableLiveData<Int>()
    val isProgress = MutableLiveData<Pair<Boolean, Int>>()
    val informationFragment = MutableLiveData<String>()
    val intentToLogin = MutableLiveData<Unit>()
    fun setRegisterUserAuth(valueRegister: Array<String?>) {

        if (utils.isEmptyValues(valueRegister)) {
            isErrorToast.value = -1
        } else {

            if (!valueRegister[4].equals(valueRegister[5])) {
                isErrorToast.value = -2
            } else {
                isProgress.value = Pair(true, 1)
                viewModelScope.launch {
                    when (val response = mainModel.setEmailAndPasswordByCreate(
                        valueRegister[3]!!,
                        valueRegister[4]!!
                    )) {
                        is ManagerError.Error -> {
                            isProgress.postValue(Pair(false, 0))
                            informationFragment.postValue(response.error)
                        }
                        is ManagerError.Success -> {
                            isProgress.postValue(Pair(true, 2))
                            setRegisterUser(
                                valueRegister.requireNoNulls(),
                                response.modelSuccess as FirebaseUser
                            )
                        }
                    }
                }
            }


        }
    }

    private suspend fun setRegisterUser(valueRegister: Array<String>, firebaseUser: FirebaseUser) {
        when (val response = mainModel.setUserFirestore(valueRegister, firebaseUser)) {
            is ManagerError.Success -> {
                mainModel.closeSession()
                isProgress.postValue(Pair(false, 1))
            }
            is ManagerError.Error -> {
                isProgress.postValue(Pair(false, 0))
                informationFragment.postValue(response.error)
            }
        }
    }

    fun setLoginUser(email: String, password: String) {
        if (email.isEmpty()) {
            isErrorToast.value = -1
            return
        }
        if (password.isEmpty()) {
            isErrorToast.value = -2
            return
        }
        isProgress.value = Pair(true, 1)
        viewModelScope.launch {

            when (val response = mainModel.getUserAuth(email, password)) {
                is ManagerError.Success -> {
                    isProgress.value = Pair(true, 2)
                    isSetPetitionParallel(response.modelSuccess as String)
                }
                is ManagerError.Error -> {
                    isProgress.postValue(Pair(false, 0))
                    informationFragment.postValue(response.error)
                }
            }
        }
    }

    private suspend fun isSetPetitionParallel(email:String) {
        when (val response = mainModel.setSession(email).getOrThrow() ){
            is ManagerError.Success -> {
                isProgress.postValue(Pair(false, response.modelSuccess as Int))
            }
            is ManagerError.Error -> {
                isProgress.postValue(Pair(false, 0))
                informationFragment.postValue(response.error)
            }
        }
    }

       fun isSetNewInstall(){
           viewModelScope.launch(Dispatchers.IO) {
               mainModel.isSetInstall(false)
           }
       }
}