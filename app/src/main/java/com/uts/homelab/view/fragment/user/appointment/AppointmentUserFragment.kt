package com.uts.homelab.view.fragment.user.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentOptionBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterAppointment
import com.uts.homelab.view.adapter.OnResult
import com.uts.homelab.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppointmentUserFragment : Fragment(), OnResult {

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

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_hospital)


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

            binding.nameUser.text = it.name + " " + it.lastName
        }

        viewModel.informationFragment.observe(viewLifecycleOwner){
            if(it == null ) return@observe
            informationDialog = InformationFragment()
            if (it == Cons.VIEW_DIALOG_INFORMATION) {
                informationDialog!!.getInstance(
                        getString(R.string.attention),
                    "Para continuar, debes terminar el proceso de registro",
                    "Continuar"
                ) {
                    informationDialog!!.dismiss()
                    clearObservers()
                    findNavController().navigate(navDirectionsCompleteData)
                }
            } else {
                informationDialog!!.getInstance(getString(R.string.attention), it)
            }

            informationDialog!!.show(requireActivity().supportFragmentManager, "${javaClass.simpleName} informationDialog")

        }

        viewModel.isProgress.observe(viewLifecycleOwner) {
            if(it==null) return@observe
            if (it.first) {

                if(it.second==2 || it.second == 3){
                    binding.loading.visibility = View.VISIBLE
                    binding.rvAppointment.visibility = View.GONE
                    binding.messageLoading.text = "CARGANDO DISPONIBILIDAD . . ."
                    if(it.second == 3){
                        binding.messageLoading.text = "No se encuentran citas disponibles para el dia de hoy"
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
            binding.rvAppointment.adapter = AdapterAppointment(it, Rol.USER,this)
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
        viewModel.isProgress.value = null
        viewModel.listAppointmentModel.value = null
        binding.countAppointment.text = "0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearObservers()
        _binding = null

    }

    override fun onSuccess(appointmentModel: AppointmentUserModel) {
        findNavController().navigate(AppointmentUserFragmentDirections.actionNavigationHomeToProcessAppointmentFragment(appointmentModel,Rol.USER.name))
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }
}