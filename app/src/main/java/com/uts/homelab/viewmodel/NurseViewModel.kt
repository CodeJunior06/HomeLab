package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.NurseModel
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse
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
    val informationFragment = MutableLiveData<String?>()
    val progressDialog = MutableLiveData<Boolean>()

    //MORE DATA
    val intentToMainNurse = MutableLiveData<Unit>()
    //PROFILE
    val modelWorkingDay = MutableLiveData<WorkingDayNurse>()
    val isService = MutableLiveData<Boolean>()
    //MAIN
    val progressDialogRv = MutableLiveData<Pair<Boolean,Int>>()
    val setRecycler =MutableLiveData<List<AppointmentUserModel>>()

    fun init() {
        viewModelScope.launch {
            val response = model.initView()
            nurseModel.postValue(response)
            if (response.newNurse) {
                informationFragment.postValue(Cons.VIEW_DIALOG_INFORMATION)
            }else{

                progressDialogRv.value  = Pair(true,2)
                when(val res = model.getAppointment()) {
                    is ManagerError.Success ->{
                        setRecycler.postValue(res.modelSuccess as List<AppointmentUserModel>)
                    }
                    is ManagerError.Error ->{
                        progressDialogRv.value  = Pair(true,3)
                    }
                }
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
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    intentToMainNurse.postValue(Unit)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(response.error)
                }

            }
        }
    }

    fun deleteNurseSession() {
        viewModelScope.launch {
            model.deleteSessionRoom()
        }
    }

    fun getJournal() {
        progressDialog.value = true

        viewModelScope.launch {
            when (val response = model.getJournalByNurse()){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    modelWorkingDay.postValue(response.modelSuccess as WorkingDayNurse)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(response.error)
                }

            }

        }
    }

    fun stopJournal() {
        progressDialog.value = true

        viewModelScope.launch {
            when (val response = model.changeJournalByNurse(modelWorkingDay.value!!,false)){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    isService.postValue(false)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(response.error)
                }

            }

        }
    }

    fun initJournal() {

        progressDialog.value = true

        viewModelScope.launch {
            when (val response = model.changeJournalByNurse(modelWorkingDay.value!!, true)){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    isService.postValue(true)

                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(response.error)
                }

            }

        }
    }

}