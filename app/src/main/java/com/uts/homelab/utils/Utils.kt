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

    fun getCurrentDate(time:Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date(time)
        return dateFormat.format(currentDate)
    }

    fun validarFechaMayorQueFechaActual(fechaString: String): Boolean {
        try {
            val formato = SimpleDateFormat("yyyy-MM-dd")
            val fechaActual = Calendar.getInstance().time
            val fecha = formato.parse(fechaString)
            val calendar1 = Calendar.getInstance()
            calendar1.time = fecha

            val calendar2 = Calendar.getInstance()
            calendar2.time = fechaActual

            // Obtiene los días del mes de cada Calendar

            // Obtiene los días del mes de cada Calendar
            val laEscogida = calendar1[Calendar.DAY_OF_MONTH]
            val laActual = calendar2[Calendar.DAY_OF_MONTH]

            // Realiza la comparación por día del mes

            // Realiza la comparación por día del mes
            if (laEscogida < laActual) {
                return false
            } else if (laEscogida > laActual) {
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            // Maneja cualquier error de formato de fecha aquí
            return false
        }
    }

    fun validarFechaMayorIgualAFechaActual(fechaString: String): Boolean {
        try {
            val formato = SimpleDateFormat("yyyy-MM-dd")
            val fechaActual = Calendar.getInstance().time
            val fecha = formato.parse(fechaString)

            // Compara las fechas

            val calendar1 = Calendar.getInstance()
            calendar1.time = fecha

            val calendar2 = Calendar.getInstance()
            calendar2.time = fechaActual

            // Obtiene los días del mes de cada Calendar

            // Obtiene los días del mes de cada Calendar
            val laEscogida = calendar1[Calendar.DAY_OF_MONTH]
            val laActual = calendar2[Calendar.DAY_OF_MONTH]

            // Realiza la comparación por día del mes

            // Realiza la comparación por día del mes
            if (laEscogida < laActual) {
               return false
            } else return true

        } catch (e: Exception) {
            // Maneja cualquier error de formato de fecha aquí
            return false
        }
    }


    fun tst(dateChoose:String,dateArray:String): Boolean {

        // Define un formato para parsear las fechas

        // Define un formato para parsear las fechas
        val formato = SimpleDateFormat("yyyy-MM-dd")

        return try {
            // Convierte las cadenas a objetos Date
            val dateChoose = formato.parse(dateChoose)
            val dateArray = formato.parse(dateArray)

            // Convierte los objetos Date a objetos Calendar
            val calendar1 = Calendar.getInstance()
            calendar1.time = dateChoose
            val calendar2 = Calendar.getInstance()
            calendar2.time = dateArray

            // Obtiene los días del mes de cada Calendar
            val diaChoose = calendar1[Calendar.DAY_OF_MONTH]
            val diaArray = calendar2[Calendar.DAY_OF_MONTH]

            // Realiza la comparación por día del mes
            if (diaChoose > diaArray) {
                true
            } else {
                 false
            }
        } catch (e: Exception) {
            System.err.println("Error al parsear las fechas: " + e.printStackTrace())
            false
        }
    }
}