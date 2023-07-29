package com.uts.homelab

import com.google.firebase.auth.FirebaseUser

class AuthSingleton {
    var uid:String = ""
    var model:FirebaseUser? = null
    var token:String? = null
    companion object {

        @Volatile
        private var instance: AuthSingleton? = null
        fun getInstance(): AuthSingleton {
            return instance ?: synchronized(this) {
                instance ?: AuthSingleton().also { instance = it }
            }
        }
    }
}