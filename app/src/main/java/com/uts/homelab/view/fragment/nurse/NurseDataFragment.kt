package com.uts.homelab.view.fragment.nurse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentNurseDataBinding
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.view.NurseActivity
import com.uts.homelab.viewmodel.NurseViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NurseDataFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNurseDataBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker: Marker? = null

    private val viewModel: NurseViewModel by activityViewModels()

    private lateinit var progressFragment: ProgressFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNurseDataBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setModel(NurseDataFragmentArgs.fromBundle(requireArguments()).nurseModel)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        observers()

        binding.btnSave.setOnClickListener {

            viewModel.setCompleteRegister(
                arrayOf(
                    binding.etEdad.text.toString(),
                    binding.etExperiencia.text.toString(),
                    binding.etAddress.text.toString(),
                    binding.etIdVehicule.text.toString(),
                    marker!!.position.latitude.toString(),
                    marker!!.position.longitude.toString()
                )
            )
        }

    }

    private fun observers() {
        viewModel.nurseModel.observe(viewLifecycleOwner) {
            binding.nameNurse.text = "${it.name} ${it.lastName}"
        }
        viewModel.toast.observe(viewLifecycleOwner){
            toastMessage("Rellenar los campos vacios")
        }

        viewModel.progressDialog.observe(viewLifecycleOwner){
            if(it){
                progressFragment = ProgressFragment.getInstance("Actualizando datos del enfermero")
                progressFragment.show(requireActivity().supportFragmentManager,"")
            }else{
                if(progressFragment.isVisible){
                    progressFragment.dismiss()
                }
            }
        }

        viewModel.intentToMainNurse.observe(viewLifecycleOwner){

        }
    }

    private var bool = false
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false

        googleMap.setOnMapClickListener { latLng -> // Aquí puedes manejar la acción cuando se hace click en el mapa

            binding.btnSave.isEnabled = true
            if (!bool) {
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        googleMap.cameraPosition.zoom
                    )
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                bool = true


                marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Mi Ubicación")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.nurse_women_location))
                )
            } else {
                marker!!.position = latLng
            }

        }

        fusedLocationClient.lastLocation.addOnSuccessListener(
            requireActivity()
        ) { location ->
            if (marker != null) {
                marker!!.remove()
            }
            Log.e("SANTI", "location succes" + location.longitude + location.latitude)
            val l = LatLng(location.latitude, location.longitude)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15.0f))
        }


    }

}