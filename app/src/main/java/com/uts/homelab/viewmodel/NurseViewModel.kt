package com.uts.homelab.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.NurseModel
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerAppointmentUserModel
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
    val intent = MutableLiveData<Unit>()
    //PROFILE
    val modelWorkingDay = MutableLiveData<WorkingDayNurse>()
    val isService = MutableLiveData<Boolean>()
    //MAIN
    val progressDialogRv = MutableLiveData<Pair<Boolean,Int>>()
    val setRecycler =MutableLiveData<List<AppointmentUserModel>>()
    //ASYNC APPOINTMENT
    val asyncAppointment =MutableLiveData<AppointmentUserModel>()

    fun init() {
        viewModelScope.launch {
            val response = model.initView()
            nurseModel.postValue(response)
            if (response.newNurse) {
                informationFragment.postValue(Cons.VIEW_DIALOG_INFORMATION)
            }else{

                progressDialogRv.value  = Pair(true,2)
                when(val res = model.getAppointment()) {
                    is ManagerAppointmentUserModel.Success ->{
                        setRecycler.postValue(res.modelSuccess)
                    }
                    is ManagerAppointmentUserModel.Error ->{
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
                    intent.postValue(Unit)
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
            when(val res =model.deleteSessionRoom()){
                is ManagerError.Success ->{
                    intent.postValue(Unit)
                }
                is ManagerError.Error -> {
                    informationFragment.postValue(res.error)
                }
            }
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

    fun updateDataProfile(values: Array<String?>) {
        if (Utils().isEmptyValues(values)){
            return
        }
        progressDialog.value = true
        viewModelScope.launch {
            when (val response = model.updateDataNurseFirestore(nurseModel.value!!, values)){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    nurseModel.postValue(response.modelSuccess as NurseRegister)
                    informationFragment.postValue(Cons.UPDATE_DATA_NURSE)

                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(response.error)
                }

            }

        }

    }

    fun changePassword() {
        progressDialog.value = true

        viewModelScope.launch {

            when(val res = model.sendRequestChangePassword()){
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(Cons.UPDATE_PASSWORD)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(res.error)

                }
            }
        }
    }

    fun setMessageOpinion(type: String, message: String, title: String) {
        progressDialog.value = true
        viewModelScope.launch {
            when (val res = model.setOpinion(type, message,title)) {
                is ManagerError.Success -> {
                    progressDialog.postValue(false)
                }
                is ManagerError.Error -> {
                    progressDialog.postValue(false)
                    informationFragment.postValue(res.error)
                }
            }
        }

    }

    private val onCall = { appointment: AppointmentUserModel ->
        Log.i("ONCALL", "PASAMOS")
        asyncAppointment.value = appointment
    }
    fun initAsyncAppointment() {
        model.initAsyncAppointmentByNurse(onCall)
    }

}