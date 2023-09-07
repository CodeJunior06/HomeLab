package com.uts.homelab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uts.homelab.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class ProfileUserViewModel @Inject constructor(private val model: UserModel) : ViewModel() {

    val exitSession = MutableLiveData<Unit>()
    fun exitUserSession() {
        viewModelScope.launch( Dispatchers.Main) {
            if (model.closeSession()) {
                exitSession.postValue(Unit)
            }
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}