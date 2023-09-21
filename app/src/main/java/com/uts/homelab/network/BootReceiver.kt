package com.uts.homelab.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // El dispositivo se ha reiniciado; inicia tu servicio de ubicación aquí
            val serviceIntent = Intent(context, LocationService::class.java)
            context?.startService(serviceIntent)
        }
    }
}