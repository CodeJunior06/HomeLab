package com.uts.homelab.view.fragment.user.processappointment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentProcessAppointmentBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.network.dataclass.DirectionsResponse
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.AppointmentUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessAppointmentFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentProcessAppointmentBinding
    private val viewModel: AppointmentUserViewModel by activityViewModels()
    private lateinit var googleMap: GoogleMap

    private var marker: Marker? = null
    private var markerNurse: Marker? = null
    private var bool = false

    private var progressDialog: ProgressFragment = ProgressFragment()
    private var informationFragment: InformationFragment? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var model: AppointmentUserModel
    private lateinit var typeUser: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model = ProcessAppointmentFragmentArgs.fromBundle(requireArguments()).appointmentModel
        typeUser = ProcessAppointmentFragmentArgs.fromBundle(requireArguments()).typeUser

        if (typeUser == Rol.USER.name) {
            binding.btnAction.visibility = View.GONE
            viewModel.initAsyncAppointment(model.uidNurse, model.uidUser, model.dc)

            binding.etName.setText(model.modelNurse.name + " " + model.modelNurse.lastName)
            binding.etPhone.setText(model.phone.toString())
        } else {
            binding.etName.setText(model.modelUser.name + " " + model.modelUser.lastName)
            binding.etPhone.setText(model.modelUser.phone.toString())
        }



        binding.btnAction.setOnClickListener {

            if (binding.btnAction.text == "INICIAR") {
                viewModel.initProcessAppointment(model)
            } else {
                viewModel.initProcessAppointmentFinishStepOne(model)
            }
        }

        binding.etAddress.setText(model.address)


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setObservers()

        super.onViewCreated(view, savedInstanceState)
        printState(model.state)
    }

    private fun printState(state: String) {
        when (state) {
            State.CURSO.name -> {
                viewModel.initAsyncWorkingDay(model.uidNurse)
                binding.btnAction.text = "Llegue al destino"
                binding.btnAction.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue_alianza
                    )
                )
            }
            State.CITA.name -> {
                model.state = State.CITA.name
                model.step = 2
                try {
                    clear()
                    findNavController().navigate(
                        ProcessAppointmentFragmentDirections.actionProcessAppointmentFragmentToProcessSecondAppointmentFragment(
                            typeUser,
                            model
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        printState(model.state)
    }

    private fun setObservers() {

        viewModel.isProgress.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            if (it.first) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager, javaClass.name)
            } else {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }

                if (it.second == 2) {
                    binding.btnAction.text = "Llegue al destino"
                    binding.btnAction.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue_alianza
                        )
                    )
                }

                if (it.second == 3) {
                    model.state = State.CITA.name
                    model.step = 2
                    clear()
                    findNavController().navigate(
                        ProcessAppointmentFragmentDirections.actionProcessAppointmentFragmentToProcessSecondAppointmentFragment(
                            typeUser,
                            model
                        )
                    )
                }
            }
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            informationFragment = InformationFragment()
            if (it == Cons.UPDATE_DATA_NURSE) {
                informationFragment!!.getInstance(
                    getString(R.string.correct),
                    "Se han actualizado correctamente los datos"
                )
            } else {
                informationFragment!!.getInstance(getString(R.string.attention), it)
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment!!.isVisible) {
                        informationFragment!!.dismiss()
                    }
                }
            }, 3500)

            if (informationFragment!!.isVisible) {
                informationFragment!!.dismiss()
            }

            informationFragment!!.show(
                requireActivity().supportFragmentManager,
                "InformationFragment"
            )
        }


        viewModel.modelAppointment.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            printState(it.state)
        }
        viewModel.modelWorkingDay.observe(viewLifecycleOwner) {
            if(it==null)return@observe

            val l = LatLng(it.geolocation.latitude.toDouble(), it.geolocation.longitude.toDouble())
            if (markerNurse == null) {

                markerNurse = googleMap.addMarker(
                    MarkerOptions()
                        .position(l)
                        .title(model.modelNurse.name + model.modelNurse.lastName)
                        .draggable(false)
                        .icon(
                            if (model.modelNurse.gender == "M") BitmapDescriptorFactory.fromBitmap(
                                Utils.getBitmapFromXml(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_nurse_men3d
                                    )!!
                                )!!
                            ) else BitmapDescriptorFactory.fromBitmap(
                                Utils.getBitmapFromXml(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_nurse_women3d
                                    )!!
                                )!!
                            )
                        )
                )
            } else {
                markerNurse!!.position = l
            }

        }
    }

    private fun clear() {
        viewModel.isProgress.value = null
        viewModel.modelAppointment.value = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        clear()
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

        /*     googleMap.setOnMapClickListener { latLngCurrent ->

                 binding.btnSave.isEnabled = true
                 if (!bool) {



                 } else {
                     marker!!.position = latLngCurrent
                 }



             }
             */



        fusedLocationClient.lastLocation.addOnSuccessListener(
            requireActivity()
        ) { location ->
            if (marker != null) {
                marker!!.remove()
            }
            Log.e("SANTI", "location succes" + location.longitude + location.latitude)
            val l = LatLng(location.latitude, location.longitude)

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    l,
                    googleMap.cameraPosition.zoom
                )
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(l))
            bool = true


            val lDestine = LatLng(
                model.geolocation.latitude.toDouble(),
                model.geolocation.longitude.toDouble()
            )

            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(lDestine)
                    .title("Destino")
                    .draggable(false)
                    .icon(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_address)
                            ?.let { Utils.getBitmapFromXml(it) }
                            ?.let { BitmapDescriptorFactory.fromBitmap(it) }

                    ))

            retroTest(l)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15.0f))
        }


    }


    private fun retroTest(latLngCurrent: LatLng) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val directionsService = retrofit.create(DirectionService::class.java)

        val origin = "${latLngCurrent.latitude},${latLngCurrent.longitude}"
        val destination = "${model.geolocation.latitude},${model.geolocation.longitude}"
        val apiKey = getString(R.string.google_maps_key)

        val call = directionsService.getDirections(origin, destination, apiKey)

        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                toastMessage("SI SE HIZO")
                if (response.isSuccessful) {
                    val points = response.body()?.routes?.firstOrNull()?.overview_polyline?.points
                    if (!points.isNullOrEmpty()) {
                        val polylineOptions = PolylineOptions()
                            .addAll(decodePoly(points))
                        // Agrega la polilínea al mapa
                        googleMap.addPolyline(polylineOptions)
                    }
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                toastMessage("PALIDOSSSSSSSSSSSSSSSSSSSSSSSSSSS")
            }
        })


    }


    // Función para decodificar los puntos de la polilínea
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(latLng)
        }
        return poly
    }
}