package com.uts.homelab.view.fragment.nurse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentDataNurseProfileBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.viewmodel.NurseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DataNurseProfileFragment : Fragment() {

    private lateinit var binding: FragmentDataNurseProfileBinding
    private val viewModel: NurseViewModel by activityViewModels()

    private var information: InformationFragment? = null
    private val informationFragment by lazy { information!! }

    private val progressDialog = ProgressFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataNurseProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clear()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_hospital)


        viewModel.nurseModel.observe(viewLifecycleOwner) {
            binding.nameNurse.text = it.name + it.lastName
            binding.AgeNurse.setText("${it.age}")
            binding.DocumentNurse.text = it.valueDocument
            binding.emailNurse.text = it.email
            binding.phoneNurse.setText("${it.phone}")
            binding.AddressNurse.text = it.address
            binding.expNurse.setText("${it.exp}")
            binding.idMotorcycle.setText("${it.idVehicle}")
        }

        binding.updateData.setOnClickListener {
            viewModel.updateDataProfile(
                arrayOf(
                    binding.phoneNurse.text.toString(),
                    binding.AgeNurse.text.toString(),
                    binding.expNurse.text.toString(),
                    binding.idMotorcycle.text.toString()
                )
            )
        }

        setObserver()
    }

    private fun setObserver() {
        viewModel.progressDialog.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            if (it) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager, javaClass.name)
            } else {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
            }
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            information = InformationFragment()
            if (it == Cons.UPDATE_DATA_NURSE) {
                informationFragment.getInstance(
                    getString(R.string.correct),
                    "Se han actualizado correctamente los datos"
                )
            } else {
                informationFragment.getInstance(getString(R.string.attention), it)
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment.isVisible) {
                        informationFragment.dismiss()
                    }
                }
            }, 3500)

            if (informationFragment.isVisible) {
                informationFragment.dismiss()
            }

            informationFragment.show(
                requireActivity().supportFragmentManager,
                "InformationFragment"
            )
        }
    }

    fun clear(){
        viewModel.progressDialog.value = null
        viewModel.informationFragment.value = null
    }


    override fun onDestroy() {
        super.onDestroy()
        clear()
    }
}