package com.uts.homelab.view.fragment.nurse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentNurseDataBinding
import com.uts.homelab.view.NurseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NurseDataFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNurseDataBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNurseDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val act = requireActivity() as NurseActivity
            act.isViewBottomNavigation(false)
            findNavController().popBackStack()
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

        fusedLocationClient.lastLocation.addOnSuccessListener(
            requireActivity()
        ) { location ->
            Log.e("SANTI", "location succes" + location.longitude + location.latitude)
            val l = LatLng(location.latitude, location.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(l)
                    .title("Mi Ubicación")
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.nurse_women_location))
            )
            googleMap.uiSettings.isMapToolbarEnabled = false

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15.0f))
        }

    }

}