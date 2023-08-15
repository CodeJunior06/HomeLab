package com.uts.homelab.utils

import java.text.SimpleDateFormat
import java.util.*
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

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}