package com.uts.homelab.view.fragment.user.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentHistoryBinding
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterHistoryAppointment
import com.uts.homelab.viewmodel.UserViewModel
import java.util.*

class HistoryUserFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    private var progressDialog: ProgressFragment = ProgressFragment()
    private var informationDialog: InformationFragment = InformationFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clearObservers()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        viewModel.getAllAppointment()
        setObserver()
        super.onViewCreated(view, savedInstanceState)

    }

    private fun clearObservers() {
        viewModel.informationFragment.value = null
        viewModel.listAppointmentModel.value = null
    }

    private fun setObserver() {
        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            informationDialog = InformationFragment()

            informationDialog.getInstance(
                getString(R.string.attention),
                it
            )
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationDialog.isVisible) {
                        informationDialog.dismiss()
                        findNavController().popBackStack()
                    }
                }
            }, 3500)
            informationDialog.show(requireActivity().supportFragmentManager, "InformationFragment")
        }

        viewModel.listAppointmentModel.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter =
                AdapterHistoryAppointment(it, Rol.USER) { model, action ->

                    when (action) {
                        1 -> {
                            viewModel.updateStateAppointment(model, State.CANCELADO)
                        }
                        2 -> {
                            viewModel.sendReportDelayAppointment(model)
                        }
                        else -> {}
                    }
                }

        }

        viewModel.isProgress.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            if (!it.first) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }

                if (it.second == 1) {
                    viewModel.getAllAppointment()
                }
            }
        }

        viewModel.progressDialog.observe(viewLifecycleOwner) {
            if (it == null) return@observe

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearObservers()
        _binding = null
    }
}