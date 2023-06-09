package com.uts.homelab.utils.response

sealed class ManagerError{
    data class Success(val modelSuccess:Any) : ManagerError()
    data class Error(val error:String) : ManagerError()
    object IsNotInternet:ManagerError()
}


