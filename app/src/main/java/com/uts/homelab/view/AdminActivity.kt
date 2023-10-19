package com.uts.homelab.view

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.uts.homelab.R
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {

    private val viewModel: AdminViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        validLocation()

        locationManager()

    }

    private val codeLocation = 101

    private fun permissionAfterA10() {
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    validLocation()
                } else {
                    showPermissionDeniedDialog()
                }
            }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permiso usando el nuevo método
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    private fun permmissBeforeA10() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                codeLocation
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == codeLocation) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                validLocation()
            } else {
                // showPermissionDeniedDialog()
            }
        }

    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de Ubicación")
            .setMessage("Necesitamos acceso a tu ubicación para funcionar correctamente.")
            .setPositiveButton("Aceptar") { _: DialogInterface, _: Int ->
                // Volver a solicitar el permiso después de un breve intervalo
                locationManager()
            }
            .create()
            .show()
    }

    private fun locationManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionAfterA10()
        } else {
            permmissBeforeA10()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun location() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isLocationEnabled =
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false

        if (!isLocationEnabled) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(com.google.android.gms.location.LocationRequest())
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                return@addOnSuccessListener
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        // Muestra un diálogo para solicitar al usuario que habilite la ubicación
                        exception.startResolutionForResult(this, codeLocation)
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun validLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            location()
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.setUpdateLastConnection()
    }

}