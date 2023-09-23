package com.uts.homelab.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.uts.homelab.model.AdminModel
import com.uts.homelab.network.dataclass.NurseLocation
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.network.db.entity.AdminSession
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.response.ManagerError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(private val adminModel: AdminModel) : ViewModel() {

    val isUserAuth = MutableLiveData<AdminSession>()
    val intentToLogin = MutableLiveData<Unit>()

    val messageToast = MutableLiveData<Int>()
    val isProgress = MutableLiveData<Pair<Boolean, Int>>()
    val informationFragment = MutableLiveData<String>()

    val modelJournal = MutableLiveData<ArrayList<NurseLocation>>()
    val uidChange = MutableLiveData<WorkingDayNurse>()

    private val onCall = { modelWorking: WorkingDayNurse ->
            Log.e("ONCALL", "PASAMOS")
            uidChange.value = modelWorking
    }

    fun getTextUI() {
        viewModelScope.launch {
            when (val res = adminModel.getModelData()) {
                is ManagerError.Error -> {
                    informationFragment.postValue(res.error)
                }
                is ManagerError.Success -> {
                    isUserAuth.postValue(res.modelSuccess as AdminSession)
                }
            }
        }
    }

    fun deleteUserSession() {
        viewModelScope.launch {
            when (val res = adminModel.deleteModelData()) {
                is ManagerError.Error -> {
                    informationFragment.postValue(res.error)
                }
                is ManagerError.Success -> {
                    intentToLogin.postValue(res.modelSuccess as Unit)
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
                    is ManagerError.Error -> {
                        isProgress.postValue(Pair(false, 0))
                        informationFragment.postValue(res.error)
                    }
                    is ManagerError.Success -> {
                        isProgress.postValue( Pair(true, 2))

                        insertNurseFirestore(arrayListOf,res.modelSuccess as FirebaseUser)
                    }
                }
            }
        }
    }

    private suspend fun insertNurseFirestore(arrayListOf: Array<String?>, firebaseUser: FirebaseUser) {

       when ( val res = adminModel.setNurseFirestore(arrayListOf,firebaseUser)){
           is ManagerError.Error -> {
               isProgress.postValue(Pair(false, 0))
               informationFragment.postValue(res.error)
           }
           is ManagerError.Success -> {
              isProgress.postValue(Pair(false,res.modelSuccess as Int))
           }
       }
    }

    fun getNurses(){
        isProgress.value = Pair(true,1)

        viewModelScope.launch {
            when ( val res = adminModel.getWorkingDayAvailable()){
                is ManagerError.Success -> {
                    isProgress.postValue(Pair(false, 0))
                    modelJournal.postValue(res.modelSuccess as ArrayList<NurseLocation>)
                }
                is ManagerError.Error -> {
                    isProgress.postValue(Pair(false, 0))
                    informationFragment.postValue(res.error)
                }

            }
        }
    }

    fun initAsync() {
        adminModel.getNursesChangeWorkingDay(onCall)
    }
}