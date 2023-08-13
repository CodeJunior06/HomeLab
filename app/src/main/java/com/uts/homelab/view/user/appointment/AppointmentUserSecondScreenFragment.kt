package com.uts.homelab.view.user.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAppointmentUserSecondScreenBinding
import com.uts.homelab.databinding.FragmentOptionBinding
import com.uts.homelab.viewmodel.userViewmodel.AppointmentUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppointmentUserSecondScreenFragment : Fragment() {
    private var _binding: FragmentAppointmentUserSecondScreenBinding? = null
    private val appointmentUserViewModel: AppointmentUserViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAppointmentUserSecondScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        appointmentUserViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val xd = AppointmentUserSecondScreenFragmentArgs.fromBundle(requireArguments())

        binding.btnAddAppointment.setOnClickListener {
            appointmentUserViewModel.setAppointment(
                arrayOf(
                    binding.etEps.text.toString(),
                    binding.etTelefono.text.toString(),
                    binding.tvTimeSelected.text.toString(),
                    binding.etDescriIon.text.toString(),
                    xd.type
                )
            )
        }
        return root
    }
}