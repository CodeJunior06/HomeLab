package com.uts.homelab.view.fragment.user

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.uts.homelab.databinding.FragmentAddressBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.viewmodel.UserViewModel
import java.util.*


class AddressFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel: UserViewModel by activityViewModels()

    private var marker: Marker? = null
    private lateinit var googleMap: GoogleMap
    private var bool = false

    private var progressDialog: ProgressFragment = ProgressFragment()
    private var informationDialog: InformationFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val validModel = AddressFragmentArgs.fromBundle(requireArguments()).appointmentModel
        if(validModel.uidUser.isNotEmpty()){
            viewModel.setModelAppointment(validModel)
        }
        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clear()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnSave.setOnClickListener {
            if(validModel.uidUser.isNotEmpty()){
                viewModel.registerAppointment(
                    arrayOf(
                        binding.etAddress.text.toString(),
                        marker!!.position.latitude.toString(),
                        marker!!.position.longitude.toString()
                    )
                )
            }else{
                viewModel.update(
                    arrayOf(
                        binding.etAddress.text.toString(),
                        marker!!.position.latitude.toString(),
                        marker!!.position.longitude.toString()
                    )
                )
            }

        }

        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {

        viewModel.informationFragment.observe(viewLifecycleOwner) {

            if (it == null) return@observe

            informationDialog = InformationFragment()
            informationDialog!!.getInstance("ATENCION", if(it == "0") getString(R.string.data_update) else it )

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationDialog!!.isVisible) {
                        informationDialog!!.dismiss()
                    }
                    if(it == "0"){
                        viewModel.intentToMainUser.postValue(Unit)
                    }
                }
            }, 3000)

            if (!informationDialog!!.isAdded) {
                informationDialog!!.show(requireActivity().supportFragmentManager, "gg")
            }
        }

        viewModel.progressDialog.observe(viewLifecycleOwner) {
            if(it == null)return@observe
            if (it) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager, "progress ${javaClass.name}")
            } else {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
            }
        }
        viewModel.intentToMainUser.observe(viewLifecycleOwner){
            if(it == null)return@observe
            findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToNavigationHome())
        }
    }

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

    fun clear(){
        viewModel.informationFragment.value = null
        viewModel.progressDialog.value = null
        viewModel.intentToMainUser.value = null
    }

}