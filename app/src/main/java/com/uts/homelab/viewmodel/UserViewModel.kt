package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.UserModel
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerAppointmentUserModel
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val model: UserModel) : ViewModel() {

    var userModel = MutableLiveData<UserRegister?>()
    var listAppointmentModel= MutableLiveData<List<AppointmentUserModel>?>()

    val informationFragment = MutableLiveData<String?>()

    val progressDialog = MutableLiveData<Boolean>()
    val intentToMainUser = MutableLiveData<Unit>()

    val isProgress = MutableLiveData<Pair<Boolean, Int>>()

    private var appointmentUserModel: AppointmentUserModel? = null

    fun init() {
        isProgress.value  = Pair(true,2)

        viewModelScope.launch {
            val response = model.initView()
            userModel.postValue(response)
            if (response.newUser) {
                informationFragment.postValue(Cons.VIEW_DIALOG_INFORMATION)
            }else{
                when (val res = model.getAppointmentByUser()){
                    is ManagerAppointmentUserModel.Success -> {
                        listAppointmentModel.postValue(res.modelSuccess)
                    }
                    is ManagerAppointmentUserModel.Error -> {
                        isProgress.postValue(Pair(true,3))
                    }
                }
            }
        }
    }

    fun getAllAppointment(){
        progressDialog.value = true
        viewModelScope.launch {
            when(val res = model.getAppointmentAllByUser()){
                is ManagerError.Success->{
                    listAppointmentModel.postValue(res.modelSuccess as List<AppointmentUserModel> )
                    progressDialog.postValue(false)
                }
                is ManagerError.Error -> {
                    listAppointmentModel.postValue(emptyList())
                    progressDialog.postValue(false)
                    informationFragment.postValue("No has realizado la primera cita")
                }
            }



        }

    }

    fun setModel(userModel: UserRegister) {
        this.userModel.value = userModel
    }

    fun setModelAppointment(appointmentModel: AppointmentUserModel) {
        appointmentUserModel = appointmentModel
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

    fun registerAppointment(arrayOf: Array<String?>) {

        if (Utils().isEmptyValues(arrayOf)) {
            return
        }
        progressDialog.value = true
        viewModelScope.launch {

            when (val response = model.saveAppointment(arrayOf, appointmentUserModel)) {
                is ManagerError.Success -> {

                         model.updateAppointmentAvailable(
                            appointmentUserModel!!.date,
                            appointmentUserModel!!.uidNurse,
                            appointmentUserModel!!.hour,
                            appointmentUserModel!!.uidUser
                        )
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

    fun getAllAppointmentFinish() {
        progressDialog.value = true
        viewModelScope.launch {
            when(val res = model.getAllAppointmentStateFinish()){
                is ManagerAppointmentUserModel.Success->{
                    listAppointmentModel.postValue(res.modelSuccess)
                    progressDialog.postValue(false)
                }
                is ManagerAppointmentUserModel.Error -> {
                    listAppointmentModel.postValue(emptyList())
                    progressDialog.postValue(false)
                }
            }
        }
    }
}