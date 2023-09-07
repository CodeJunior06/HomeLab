package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.NurseModel
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NurseViewModel @Inject constructor(private val model:NurseModel): ViewModel() {

    var nurseModel = MutableLiveData<NurseRegister>()
    var toast = MutableLiveData<Unit>()

    val informationFragmentFragment = MutableLiveData<String?>()

    val progressDialog = MutableLiveData<Boolean>()
    val intentToMainNurse = MutableLiveData<Unit>()

    fun init() {
        viewModelScope.launch {
            val response = model.initView()
            nurseModel.postValue(response)
            if (response.newNurse) {
                informationFragmentFragment.postValue(Cons.VIEW_DIALOG_INFORMATION)
            }
        }
    }

    fun setModel(nurseModel: NurseRegister?) {
        this.nurseModel.value = nurseModel!!
    }

    fun setCompleteRegister(nurseData: Array<String?>) {
        if (Utils().isEmptyValues(nurseData)) {
            toast.value = Unit
        }
        progressDialog.value = true
        viewModelScope.launch {
            when (val response = model.setRegisterNurse(nurseData, nurseModel.value!!)) {

                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragmentFragment.postValue(response.error)
                }
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    intentToMainNurse.postValue(Unit)
                }
            }
        }
    }

    fun deleteNurseSession() {
        viewModelScope.launch {
            model.deleteSessionRoom()
        }
    }

}