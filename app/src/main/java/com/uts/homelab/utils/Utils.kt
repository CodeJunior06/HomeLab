package com.uts.homelab.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
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
    fun dateToLong(dateString: String): Long {
        try {
            // Define el formato de fecha deseado
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")

            // Parsea la fecha en formato "yyyy-MM-dd" a un objeto Date
            val date = dateFormat.parse(dateString)

            // Obtiene la representación en milisegundos desde la época
            return date?.time ?: 0
        } catch (e: Exception) {
            // Maneja cualquier error de análisis de fecha
            e.printStackTrace()
        }
        return 0
    }

    fun getCurrentDate(time: Long): String {
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


    fun tst(dateChoose: String, dateArray: String): Boolean {

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

    companion object {
        fun messageErrorConverter(code: Int): String {
            return when (code) {     -1 -> "La dirección de correo electrónico está mal formateada"
                -2 -> "La contraseña debe tener al menos 6 caracteres"
                -3 -> "No tienes Internet, intenta nuevamente"
                -100 -> "Error al eliminar la información de la base de datos"
                -101 -> "Error al actualizar la información de la base de datos"
                -102 -> "Error al insertar la información en la base de datos"
                -103 -> "Error al obtener la información de la base de datos"
                -200-> "Error en la insersion en el registro de autenticación"

                else -> "Error, contacta al soporte"
            }
        }

        fun messageErrorConverter(message:String): String{
          val code = when (message) {
              "The email address is badly formatted." -> {
                  -1
              }
              "The given password is invalid. [ Password should be at least 6 characters ]" -> {
                  -2
              }
              "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> {
                  -3
              }
              else -> 0
          }

            return messageErrorConverter(code)
        }

         fun getBitmapFromXml(drawable: Drawable): Bitmap? {
            val bitmap = Bitmap.createBitmap(
                80,
                80,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, bitmap.width, bitmap.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}