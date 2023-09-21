package com.uts.homelab.view.fragment.user.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.uts.homelab.databinding.FragmentOptionBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.TypeView
import com.uts.homelab.view.adapter.AdapterUserAppointment
import com.uts.homelab.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppointmentUserFragment : Fragment() {

    private var _binding: FragmentOptionBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: UserViewModel by activityViewModels()

    private var progressDialog: ProgressFragment = ProgressFragment()
    private var informationDialog: InformationFragment? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOptionBinding.inflate(inflater, container, false)
        val root: View = binding.root


        viewModel.init()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnAddAppointment.setOnClickListener {
         findNavController().navigate(AppointmentUserFragmentDirections.actionNavigationHomeToAppointmentUserSecondScreenFragment())
        }
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(navDirectionsProfile)
        }
        binding.btnHistoryAppointment.setOnClickListener {
            findNavController().navigate(AppointmentUserFragmentDirections.actionNavigationHomeToNavigationDashboard())
        }

        binding.btnResult.setOnClickListener {
            findNavController().navigate(AppointmentUserFragmentDirections.actionNavigationHomeToResultUserFragment())
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }

    private lateinit var  navDirectionsCompleteData:NavDirections
    private lateinit var  navDirectionsProfile:NavDirections


    private fun setObserver() {
        viewModel.userModel.observe(viewLifecycleOwner){
            navDirectionsCompleteData = AppointmentUserFragmentDirections.actionNavigationHomeToUserDataFragment(it!!)
            navDirectionsProfile = AppointmentUserFragmentDirections.actionNavigationHomeToNavigationNotifications(it)

            binding.nameUser.text = it.name
        }

        viewModel.informationFragment.observe(viewLifecycleOwner){
            if(it == null ) return@observe
            informationDialog = InformationFragment()
            if (it == Cons.VIEW_DIALOG_INFORMATION) {
                informationDialog!!.getInstance(
                    "ATENCION",
                    "Hemos detectado que faltan datos para continuar con el procesos",
                    "Ir a llenar"
                ) {
                    informationDialog!!.dismiss()
                    clearObservers()
                    findNavController().navigate(navDirectionsCompleteData)
                }
            } else {
                informationDialog!!.getInstance("ATENCION", it)
            }

            informationDialog!!.show(requireActivity().supportFragmentManager, "gg")

        }

        viewModel.isProgress.observe(viewLifecycleOwner) {
            if (it.first) {

                if(it.second==2 || it.second == 3){
                    binding.loading.visibility = View.VISIBLE
                    binding.rvAppointment.visibility = View.GONE
                    binding.messageLoading.setText("CARGANDO DISPONIBILIDAD . . .")
                    if(it.second == 3){
                        binding.messageLoading.setText("No se encuentran citas disponibles para el dia de hoy")
                    }
                }

            } else {
                if(it.second==2){
                    binding.loading.visibility = View.GONE
                    binding.rvAppointment.visibility = View.VISIBLE
                }
            }
        }

        viewModel.listAppointmentModel.observe(viewLifecycleOwner){
            if(it ==null ) return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter = AdapterUserAppointment(it, TypeView.MAIN,AdapterUserAppointment.VIEW_USER)
            binding.countAppointment.text = it.size.toString()
            if(it.isEmpty()){
                viewModel.isProgress.postValue(Pair(true,3))
            }else{
                binding.loading.visibility = View.GONE
                binding.rvAppointment.visibility = View.VISIBLE
            }
        }


    }
    private fun clearObservers(){
        viewModel.informationFragment.value = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        clearObservers()

    }
}