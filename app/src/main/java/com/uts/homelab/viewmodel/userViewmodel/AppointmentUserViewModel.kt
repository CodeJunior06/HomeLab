package com.uts.homelab.viewmodel.userViewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.model.AppointmentUserModel
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class AppointmentUserViewModel @Inject constructor(private val appointmentUserModel: AppointmentUserModel) : ViewModel() {

    @Singleton
    val isProgress = MutableLiveData<Pair<Boolean, Int>>()
    val informationFragment = MutableLiveData<String>()

    private val _text = MutableLiveData<String>().apply {
        value = "Agendar cita"
    }
    val text: LiveData<String> = _text

    fun setAppointment(valueAppointment: Array<String?>){
        viewModelScope.launch {
            setAppointmentUser(
                valueAppointment.requireNoNulls()
            )
        }
    }
    private suspend fun setAppointmentUser(valueAppointment: Array<String>) {
        when (val response = appointmentUserModel.setAppointmentUserFirestore(valueAppointment)) {
            is ManagerError.Success -> {
                isProgress.postValue(Pair(false, 1))
            }
            is ManagerError.Error -> {
                isProgress.postValue(Pair(false, 0))
                informationFragment.postValue(response.error)
            }
            else -> {
                isProgress.postValue(Pair(false, 0))
            }
        }
    }
}