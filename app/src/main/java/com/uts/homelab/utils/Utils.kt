package com.uts.homelab.utils

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