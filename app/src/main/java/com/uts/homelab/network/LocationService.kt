package com.uts.homelab.network

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.CoroutinesRoom
import androidx.room.RoomDatabase
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.uts.homelab.di.RoomModule
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.network.db.DataBaseHome
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class LocationService : Service() {

    private val NOTIFICATION_ID = 12345
    private val NOTIFICATION_CHANNEL_ID = "LocationChannel"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var context:Context
    private val db = FirebaseFirestore.getInstance()


    private var longitud = 0.0
    private var latitud = 0.0

    private var latLng: LatLng? = null

    var coordinateOld = arrayOfNulls<Double>(2)
    var coordinateNew = arrayOfNulls<Double>(2)

    private val serviceScope = CoroutineScope(Dispatchers.IO)



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startLocationUpdates()
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Running in background")
            .setSmallIcon(com.uts.homelab.R.drawable.ic_lock)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification,FOREGROUND_SERVICE_TYPE_LOCATION)
        }else{
            startForeground(NOTIFICATION_ID,notification)
        }
    }

    private fun startLocationUpdates() {

        // Configurar la detección de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(10000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->

                    if (this@LocationService.latitud != location.latitude && this@LocationService.longitud != location.longitude) {
                        Log.d("Latitude old ", this@LocationService.latitud.toString())
                        Log.d("Longitude old", this@LocationService.longitud.toString())
                        coordinateOld[0]= this@LocationService.latitud
                        coordinateOld[1] = this@LocationService.longitud
                        coordinateNew[0] = location.latitude
                        coordinateNew[1] = location.longitude
                        this@LocationService.latitud = location.latitude
                        this@LocationService.longitud = location.longitude
                        Log.d("Latitude new ", this@LocationService.latitud.toString())
                        Log.d("Longitude new ", this@LocationService.longitud.toString())

                        if (!methodAproximate()) return
                        latLng = LatLng(latitud, longitud)

                        serviceScope.launch {
                            val model = WorkingDayNurse()
                            model.id = FirebaseAuth.getInstance().uid!!
                            model.active = true
                            model.geolocation.latitude = latLng!!.latitude.toString()
                            model.geolocation.longitude = latLng!!.longitude.toString()

                            val id  = db.collection("WorkingDay").whereEqualTo("id",FirebaseAuth.getInstance().uid).get().await().documents[0].id
                            db.collection("WorkingDay").document(id).set(model, SetOptions.merge())
                        }
                    }



                    // Procesar la ubicación obtenida
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.e("LOCALIZATION", latitude.toString() + longitude.toString())

                }
            }
        }

        // Manejar los permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Detener el servicio si no se tienen permisos
            stopSelf()
            return
        }

        // Iniciar la detección de ubicación
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun methodAproximate(): Boolean {
        return coordinateOld[0]!! - coordinateNew[0]!! == -600.0 && coordinateOld[0]!! - coordinateNew[0]!! == 600.0 && coordinateOld[1]!! - coordinateNew[1]!! == -600.0 && coordinateOld[1]!! - coordinateNew[1]!! == 600.0
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la detección de ubicación cuando el servicio se destruye
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}