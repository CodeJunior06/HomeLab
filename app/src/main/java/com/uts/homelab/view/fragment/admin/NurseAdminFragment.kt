package com.uts.homelab.view.fragment.admin

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
import com.uts.homelab.databinding.FragmentNurseAdminBinding
import com.uts.homelab.network.dataclass.NurseLocation
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.viewmodel.AdminViewModel

class NurseAdminFragment : Fragment(), OnMapReadyCallback {


    private lateinit var binding: FragmentNurseAdminBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val map = mapOf<String,Marker>()

    private val viewModel:AdminViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNurseAdminBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        }

        setObserver()

        super.onViewCreated(view, savedInstanceState)
    }
    private val lstPair = ArrayList<Pair<NurseLocation,Marker>>()
    private fun setObserver() {
        viewModel.modelNurseLocation.observe(viewLifecycleOwner){
            if(it==null)return@observe
            lstPair.clear()
            for(nurseLocation in it){
                val l = LatLng(nurseLocation.geolocation.latitude!!.toDouble(), nurseLocation.geolocation.longitude!!.toDouble())
                val mark = googleMap.addMarker(
                    MarkerOptions()
                        .position(l)
                        .title(nurseLocation.nameUser + nurseLocation.lastName.split(" ")[0])
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.nurse_women_location))
                )
                val pair = Pair(nurseLocation,mark!!)
                lstPair.add(pair)
            }

            viewModel.initAsync()
        }

        viewModel.uidChange.observe(viewLifecycleOwner){workingDay ->
            if(workingDay==null)return@observe

                if(!existInit(workingDay)){

                    val nurseLocation = NurseLocation()
                    nurseLocation.geolocation = workingDay.geolocation
                    nurseLocation.phone = ""
                    nurseLocation.nameUser = workingDay.id
                    nurseLocation.uidWorking = workingDay.id
                    val l = LatLng(workingDay.geolocation.latitude!!.toDouble(), workingDay.geolocation.longitude!!.toDouble())
                    val mark = googleMap.addMarker(
                        MarkerOptions()
                            .position(l)
                            .title(nurseLocation.nameUser + nurseLocation.lastName.split(" ")[0])
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.nurse_women_location))
                    )
                    val pair = Pair(nurseLocation,mark!!)
                    lstPair.add(pair)
                }
            }

    }

    private fun existInit(workingDay: WorkingDayNurse):Boolean{
        var bool = false
        var boolDelete = false
        var lst:Pair<NurseLocation,Marker>? = null
        lstPair.forEach {

            if (it.first.uidWorking.equals(workingDay.id)) {
                bool=true
                val l = LatLng(workingDay.geolocation.latitude!!.toDouble(), workingDay.geolocation.longitude!!.toDouble())
                it.second.position = l
            }

            //REVISAR POR QUE SE ELIMINAN TODOS
            if(!workingDay.active){
                boolDelete = true
                it.second.remove()
                lst = it
            }
        }
        if(boolDelete){
            lstPair.remove(lst!!)
        }
        return bool
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
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
        map.isMyLocationEnabled = true
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
                    .icon(BitmapDescriptorFactory.defaultMarker())
            )
            googleMap.uiSettings.isMapToolbarEnabled = false

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15.0f))
            viewModel.getNurses()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        clear()
    }

    private fun clear() {
        viewModel.modelNurseLocation.value = null
        viewModel.uidChange.value = null
    }

}