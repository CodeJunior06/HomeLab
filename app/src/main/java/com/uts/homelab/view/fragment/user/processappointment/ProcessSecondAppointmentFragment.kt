package com.uts.homelab.view.fragment.user.processappointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentProcessSecondAppointmentBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.fragment.user.appointment.AppointmentUserSecondScreenFragmentDirections
import com.uts.homelab.viewmodel.AppointmentUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ProcessSecondAppointmentFragment : Fragment() {

    private lateinit var binding: FragmentProcessSecondAppointmentBinding
    private val viewModel: AppointmentUserViewModel by activityViewModels()

    private lateinit var model: AppointmentUserModel
    private lateinit var typeUser: String

    private var progressDialog: ProgressFragment = ProgressFragment()
    private var informationFragment: InformationFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProcessSecondAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        model = ProcessAppointmentFragmentArgs.fromBundle(requireArguments()).appointmentModel
        typeUser = ProcessAppointmentFragmentArgs.fromBundle(requireArguments()).typeUser

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){}

        if (typeUser == Rol.USER.name) {
            binding.btnEnd.text = "Regresar al menu"
        }

        binding.btnEnd.setOnClickListener {
            if(binding.btnEnd.text.toString() == "Regresar al menu"){
                findNavController().navigate(ProcessSecondAppointmentFragmentDirections.actionProcessSecondAppointmentFragmentToNavigationInit())
            }else{
                viewModel.updateProcessAppointment(model)
            }
        }
        binding.btnSend.setOnClickListener {
            viewModel.setProblemAppointment(
                model,
                binding.title.text.toString(),
                binding.editTextTextMultiLine.text.toString()
            )
        }


        viewModel.initAsyncAppointment(model.uidNurse, model.uidUser, model.dc)
        seeObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun seeObserver() {
        viewModel.isProgress.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            if (it.first) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager, javaClass.simpleName)
            } else {

                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                if (it.second == 1) {
                    viewModel.informationFragment.postValue(Cons.FINISH_VISIT_APPOINTMENT)
                }
            }
        }
        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            informationFragment = InformationFragment()
            if (it == Cons.SEND_OPINION || it == Cons.FINISH_VISIT_APPOINTMENT) {
                informationFragment!!.getInstance(
                    getString(R.string.correct),
                    it
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

                    if (it == Cons.FINISH_VISIT_APPOINTMENT) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            findNavController().navigate(ProcessSecondAppointmentFragmentDirections.actionProcessSecondAppointmentFragmentToNavigationInit())
                        }
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

            if (it.state == State.LABORATORIO.name) {
                viewModel.informationFragment.postValue(Cons.FINISH_VISIT_APPOINTMENT)
            }
        }
    }


}