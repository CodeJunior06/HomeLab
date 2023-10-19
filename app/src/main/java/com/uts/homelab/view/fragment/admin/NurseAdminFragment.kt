package com.uts.homelab.view.fragment.admin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentNurseAdminBinding
import com.uts.homelab.network.dataclass.NurseLocation
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class NurseAdminFragment : Fragment(), OnMapReadyCallback {


    private lateinit var binding: FragmentNurseAdminBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: AdminViewModel by activityViewModels()

    private val progressDialog = ProgressFragment()
    private val informationFragment = InformationFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNurseAdminBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.modelNurseLocation.value = null

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

        setObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private val lstPair = ArrayList<Pair<NurseLocation, Marker>>()
    private fun setObserver() {


        viewModel.listNurseLocation.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            val builder = LatLngBounds.builder()
            lstPair.clear()

            for (nurseLocation in it) {
                markOnMap(nurseLocation, builder)
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 5))
            viewModel.initAsync()

            googleMap.setOnInfoWindowClickListener { marker ->
                val nurse = marker.tag as NurseLocation
                val l = LatLng(
                    nurse.geolocation.latitude.toDouble(),
                    nurse.geolocation.longitude.toDouble()
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 17F))
            }
        }

        viewModel.uidChange.observe(viewLifecycleOwner) { workingDay ->
            if (workingDay == null) return@observe

            if (!existInit(workingDay)) {
                viewModel.getNurseWorkingDayById(workingDay)
            }
        }

        viewModel.modelNurseLocation.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            markOnMap(it, null)
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe


            informationFragment.getInstance(
                getString(R.string.attention),
                it
            )

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    informationFragment.dismiss()
                }
            }, 3000)

            informationFragment.showNow(
                childFragmentManager,
                javaClass.simpleName
            )
        }
        viewModel.isProgress.observe(viewLifecycleOwner){
            if (it == null) return@observe

            when (it.first) {
                true -> {
                    if (progressDialog.fragmentManager !=null) {
                        progressDialog.dismissNow()
                    }

                    progressDialog.showNow(
                        requireActivity().supportFragmentManager,
                        "ProgressDialog"
                    )
                }
                false -> {
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }

                }
            }

        }

    }

    private fun markOnMap(nurseLocation: NurseLocation, builder: LatLngBounds.Builder?) {
        val l = LatLng(
            nurseLocation.geolocation.latitude.toDouble(),
            nurseLocation.geolocation.longitude.toDouble()
        )
        val mark = googleMap.addMarker(
            MarkerOptions()
                .position(l)
                .title(nurseLocation.nameUser.split(" ")[0] + " " + nurseLocation.lastName.split(" ")[0])
                .draggable(false)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Utils.getBitmapFromXml(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_nurse_men3d
                            )!!
                        )!!
                    )
                )
        )!!

        builder?.include(l)
        mark.tag = nurseLocation
        mark.showInfoWindow()

        val pair = Pair(nurseLocation, mark)
        lstPair.add(pair)
    }

    private fun existInit(workingDay: WorkingDayNurse): Boolean {
        var bool = false
        var boolDelete = false
        var lst: Pair<NurseLocation, Marker>? = null
        lstPair.forEach {

            if (it.first.uidWorking == workingDay.id) {
                bool = true
                val l = LatLng(
                    workingDay.geolocation.latitude.toDouble(),
                    workingDay.geolocation.longitude.toDouble()
                )
                it.second.position = l

                if (!workingDay.active) {
                    boolDelete = true
                    it.second.remove()
                    lst = it
                }
            }
        }
        if (boolDelete) {
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

        viewModel.getNursesWorkingDay()

        googleMap.uiSettings.isMapToolbarEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        clear()
    }

    private fun clear() {
        viewModel.listNurseLocation.value = null
        viewModel.uidChange.value = null
        viewModel.modelNurseLocation.value = null
        viewModel.informationFragment.value = null
        viewModel.isProgress.value = null
    }

}