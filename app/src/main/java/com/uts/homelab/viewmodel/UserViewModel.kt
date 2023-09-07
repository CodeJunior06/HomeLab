package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.UserModel
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val model: UserModel) : ViewModel() {

    var userModel = MutableLiveData<UserRegister?>()

    var toast = MutableLiveData<Unit>()

    val informationFragment = MutableLiveData<String?>()

    val progressDialog = MutableLiveData<Boolean>()
    val intentToMainUser = MutableLiveData<Unit>()

    fun init() {
        viewModelScope.launch {
            val response = model.initView()
            userModel.postValue(response)
            if (response.newUser) {
                informationFragment.postValue(Cons.VIEW_DIALOG_INFORMATION)
            }
        }
    }

    fun setModel(userModel: UserRegister) {
        this.userModel.value = userModel
    }

    fun saveRoom() {
        viewModelScope.launch {
            model.saveUserRoom(userModel.value!!)
        }
    }

    fun update(arrayOf: Array<String?>) {
        if (Utils().isEmptyValues(arrayOf)) {
            return
        }
        progressDialog.value = true
        viewModelScope.launch {

            when (val response = model.saveUserFirestore(arrayOf, userModel.value)) {
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                     informationFragment.postValue(response.modelSuccess as String)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(response.error)
                }
            }
        }
    }
}