package com.uts.homelab

class AuthSingleton {
    var uid: String = ""
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