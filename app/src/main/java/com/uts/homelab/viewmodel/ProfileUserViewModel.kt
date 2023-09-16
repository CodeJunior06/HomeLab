package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.UserModel
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileUserViewModel @Inject constructor(private val model: UserModel) : ViewModel() {

    val exitSession = MutableLiveData<Unit>()
    var modelUser = MutableLiveData<UserRegister>()

    var progressDialog = MutableLiveData<Boolean>()
    var informationDialog = MutableLiveData<String>()
    fun exitUserSession() {
        viewModelScope.launch(Dispatchers.Main) {
            if (model.closeSession()) {
                exitSession.postValue(Unit)
            }
        }
    }

    fun setModel(userModel: UserRegister) {
        modelUser.value = userModel
    }

    fun setMessageOpinion(type: String, it: String) {
        progressDialog.value = true
        viewModelScope.launch {
            when (model.setOpinion(type, it)) {
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)

                }
            }
        }

    }

    fun setRequestChangePassword() {
        progressDialog.value = true

        viewModelScope.launch {

            when(model.sendRequestChangePassword()){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    informationDialog.postValue("Se ha enviado un correo para la continuacion del restablecimiento")
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)

                }
            }
        }
    }

    fun updateData(values: Array<String?>) {

        if(Utils().isEmptyValues(values)) return

        progressDialog.value = true


        viewModelScope.launch {

            when( model.updateDataUserFirestore(values,modelUser.value!!)){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    informationDialog.postValue("Se han cambiado los datos correctamente")
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)

                }
            }
        }

    }

}