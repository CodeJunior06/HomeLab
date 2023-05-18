package com.uts.homelab.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.MainModel
import com.uts.homelab.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainViewModel @Inject constructor(private val mainModel: MainModel): ViewModel() {


    @Singleton
    var utils = Utils()

    val isErrorToast = MutableLiveData<Int>()
    fun setRegisterUser(valueRegister: Array<String?>) {

        if(utils.isEmptyValues(valueRegister)){
            isErrorToast.value = -1
        }else{

            if(!valueRegister[4].equals(valueRegister[5])){
                isErrorToast.value = -2
            }else{
                viewModelScope.launch {
                    mainModel.setEmailAndPasswordByCreate(valueRegister[3]!!,valueRegister[4]!!)
                }
            }


        }
    }
}