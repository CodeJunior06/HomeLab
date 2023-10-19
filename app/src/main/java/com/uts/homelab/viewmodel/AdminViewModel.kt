package com.uts.homelab.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.model.AdminModel
import com.uts.homelab.network.dataclass.*
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerAppointmentUserModel
import com.uts.homelab.utils.response.ManagerCommentType
import com.uts.homelab.utils.response.ManagerError
import com.uts.homelab.view.adapter.OnResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(private val adminModel: AdminModel) : ViewModel() {
    //MODULE ADMIN
    val isUserAuth = MutableLiveData<AdminSession>()
    val intentToLogin = MutableLiveData<Unit>()

    //MODULE ALL
    val messageToast = MutableLiveData<Int>()
    val isProgress = MutableLiveData<Pair<Boolean, Int>>()
    val informationFragment = MutableLiveData<String>()

    //MODULE LOCATION
    val listNurseLocation = MutableLiveData<ArrayList<NurseLocation>>()
    val modelNurseLocation = MutableLiveData<NurseLocation>()
    val uidChange = MutableLiveData<WorkingDayNurse>()

    //MODULE ONLY LOCATION
    val rvNurseWorkingAdapter = MutableLiveData<List<NurseWorkingAdapter>>()

    //MODULE PQRS
    val rvCommentType = MutableLiveData<List<CommentType>>()
    val lstAllCommentType = MutableLiveData<List<CommentType>>()

    //MODULE LABORATORIO
    val rvAppointmentUserModel = MutableLiveData<List<AppointmentUserModel>>()


    private var onCall = { modelWorking: WorkingDayNurse? ->
        Log.i("ONCALL", "PASAMOS")
            uidChange.value = modelWorking
    }

    fun getTextUI(isProgressView: Boolean) {
        if (isProgressView) {
            isProgress.value = Pair(true, 1)
        }

        viewModelScope.launch {
            when (val res = adminModel.getModelData()) {
                is ManagerError.Success -> {
                    isUserAuth.postValue(res.modelSuccess as AdminSession)
                    if (isProgressView) {
                        isProgress.value = Pair(false, 0)
                    }
                }
                is ManagerError.Error -> {
                    if (isProgressView) {
                        isProgress.value = Pair(true, 0)
                    }
                    informationFragment.postValue(res.error)

                }
            }
        }
    }

    fun getProfileInfo() {
        isProgress.value = Pair(true, 1)
        viewModelScope.launch {
            when (val res = adminModel.updateModelDataFirestore()) {
                is ManagerError.Success -> {
                    isProgress.value = Pair(false, 0)
                    getTextUI(true)
                }
                is ManagerError.Error -> {
                    isProgress.value = Pair(false, 0)
                    informationFragment.postValue(res.error)
                }
            }
        }
    }

    fun deleteUserSession() {
        viewModelScope.launch {
            when (val res = adminModel.deleteModelData()) {
                is ManagerError.Success -> {
                    intentToLogin.postValue(res.modelSuccess as Unit)
                }
                is ManagerError.Error -> {
                    informationFragment.postValue(res.error)
                }
            }
        }
    }

    fun insertNurse(arrayListOf: Array<String?>) {
        if (Utils().isEmptyValues(arrayListOf)) {
            messageToast.value = -1
        } else {
            isProgress.value = Pair(true, 1)
            viewModelScope.launch {
                when (val res = adminModel.setRegisterNurse(
                    arrayListOf[2].toString(),
                    arrayListOf[3].toString()
                )) {
                    is ManagerError.Success -> {
                        isProgress.postValue(Pair(true, 2))
                        insertNurseFirestore(arrayListOf, res.modelSuccess as FirebaseUser)
                    }
                    is ManagerError.Error -> {
                        isProgress.postValue(Pair(false, 0))
                        informationFragment.postValue(res.error)
                    }

                }
            }
        }
    }

    private suspend fun insertNurseFirestore(
        arrayListOf: Array<String?>,
        firebaseUser: FirebaseUser
    ) {

        when (val res = adminModel.setNurseFirestore(arrayListOf, firebaseUser)) {
            is ManagerError.Error -> {
                isProgress.postValue(Pair(false, 0))
                informationFragment.postValue(res.error)
            }
            is ManagerError.Success -> {
                isProgress.postValue(Pair(false, res.modelSuccess as Int))
            }
        }
    }

    fun getNursesWorkingDay() {
        isProgress.value = Pair(true, 1)

        viewModelScope.launch {
            when (val res = adminModel.getWorkingDayAvailable()) {
                is ManagerError.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    listNurseLocation.postValue(res.modelSuccess as ArrayList<NurseLocation>)
                }
                is ManagerError.Error -> {
                    isProgress.postValue(Pair(false, 0))
                    informationFragment.postValue(res.error)
                    initAsync()
                }

            }
        }
    }

    fun getNurseWorkingDayById(workingDay: WorkingDayNurse) {
        isProgress.value = Pair(true, 1)

        viewModelScope.launch {
            when (val res = adminModel.getNurseAvailableByListIds(listOf(workingDay))) {
                is ManagerError.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    val rta = res.modelSuccess as ArrayList<NurseLocation>

                    if(rta.isNotEmpty()){
                        modelNurseLocation.postValue(res.modelSuccess[0])
                    }

                }
                is ManagerError.Error -> {
                    Log.e(javaClass.name, res.error)
                }

            }
        }
    }

    fun getAllNurseWorkingDay() {
        isProgress.value = Pair(true, 1)

        viewModelScope.launch {
            when (val res = adminModel.getAllWorkingDay()) {
                is ManagerError.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    rvNurseWorkingAdapter.postValue(res.modelSuccess as ArrayList<NurseWorkingAdapter>)
                }
                is ManagerError.Error -> {
                    isProgress.postValue(Pair(false, 0))
                }

            }
        }
    }

    fun getAllPQRS() {
        isProgress.value = Pair(true, 1)

        viewModelScope.launch {
            when (val res = adminModel.getAllOpinionPQRS()) {
                is ManagerCommentType.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    rvCommentType.postValue(res.modelSuccess)
                    lstAllCommentType.postValue(res.modelSuccess)
                }
                is ManagerCommentType.Error -> {
                    isProgress.postValue(Pair(false, 0))
                }

            }
        }
    }

    fun getAppointmentLaboratory() {
        isProgress.value = Pair(true, 1)

        viewModelScope.launch {
            when (val res = adminModel.getAppointmentLaboratory()) {
                is ManagerAppointmentUserModel.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    rvAppointmentUserModel.postValue(res.modelSuccess)
                }
                is ManagerAppointmentUserModel.Error -> {
                    isProgress.postValue(Pair(false, 0))
                }

            }
        }
    }

    fun setUpdateLastConnection() {
        viewModelScope.launch {
            adminModel.setDateTimeLastConnection()
        }
    }


    fun initAsync() {
            onCall(null)
        adminModel.getNursesChangeWorkingDay(onCall)
    }
    fun stopAsync() {
        adminModel.stopAsync()
    }

    fun sendResult(appointmentModel: AppointmentUserModel) {
        isProgress.value = Pair(true, 1)

        viewModelScope.launch {
            when (val res = adminModel.createResultAppointment(appointmentModel)) {
                is ManagerError.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    getAppointmentLaboratory()
                }
                is ManagerError.Error -> {
                    isProgress.postValue(Pair(false, 0))
                    informationFragment.postValue(res.error)
                }

            }
        }
    }
}