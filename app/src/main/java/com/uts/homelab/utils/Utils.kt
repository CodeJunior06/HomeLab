package com.uts.homelab.utils

import javax.inject.Singleton

@Singleton
class Utils {
    fun isEmptyValues(valueRegister: Array<String?>): Boolean {

        for (element in valueRegister) {
            if (element.isNullOrEmpty()) {
                return true
            }
        }
        return false
    }
}