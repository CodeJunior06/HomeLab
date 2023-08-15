package com.uts.homelab.utils

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Permisos @Inject constructor(@ApplicationContext private val context: Context, private val p :Permisos.d) : BroadcastReceiver()  {

    private val f:Permisos.d  by lazy { p }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            // Verificar si el proveedor de ubicación está habilitado o deshabilitado
            val locationManager =
                context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val isLocationEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ?: false

            if (!isLocationEnabled) {
                f.s()
            }
        }
    }

    interface d{
        fun s()
    }


}