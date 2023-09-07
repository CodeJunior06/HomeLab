package com.uts.homelab.view.fragment.user.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.uts.homelab.databinding.FragmentOptionBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.UserActivity
import com.uts.homelab.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppointmentUserFragment : Fragment() {

    private var _binding: FragmentOptionBinding? = null
    private lateinit var googleMap:GoogleMap
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

     /*   val textView: TextView = binding.textHome
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        viewModel.init()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnAddAppointment.setOnClickListener {
         findNavController().navigate(AppointmentUserFragmentDirections.actionNavigationHomeToAppointmentUserSecondScreenFragment())
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }

    private lateinit var  navDirections:NavDirections
    private fun setObserver() {
        viewModel.userModel.observe(viewLifecycleOwner){
            navDirections = AppointmentUserFragmentDirections.actionNavigationHomeToUserDataFragment(it!!)
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
                    val act = requireActivity() as UserActivity
                    act.isViewBottomNavigation(false)
                    informationDialog!!.dismiss()
                    clearObservers()
                    findNavController().navigate(navDirections)
                }
            } else {
                informationDialog!!.getInstance("ATENCION", it)
            }

            informationDialog!!.show(requireActivity().supportFragmentManager, "gg")

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