package com.uts.homelab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.NurseModel
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.db.Constants
import com.uts.homelab.utils.Cons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NurseViewModel @Inject constructor(private val model:NurseModel): ViewModel() {

    var nurseModel = MutableLiveData<NurseRegister>()
    private var informationFragmentFragment = MutableLiveData<String>()
    val info:LiveData<String> get() = informationFragmentFragment

    fun init() {
        viewModelScope.launch {
            val response = model.initView()
            nurseModel.postValue(response)
            if(response.isNew){
                informationFragmentFragment.postValue(Cons.VIEW_DIALOG_INFORMATION)
            }
        }

    }

}