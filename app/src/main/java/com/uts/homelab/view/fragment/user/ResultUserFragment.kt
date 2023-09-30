package com.uts.homelab.view.fragment.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentResultUserBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterUserResult
import com.uts.homelab.viewmodel.AppointmentUserViewModel
import com.uts.homelab.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ResultUserFragment : Fragment() {

    private lateinit var binding: FragmentResultUserBinding
    private val viewModel: UserViewModel by activityViewModels()

    private var information: InformationFragment? = null
    private val informationFragment by lazy { information!! }

    private val progressDialog = ProgressFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clear()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        viewModel.getAllAppointmentFinish()

        setObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        viewModel.listAppointmentModel.observe(viewLifecycleOwner) {
            if(it==null)return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter = AdapterUserResult(it,Rol.USER){

            }
        }
        viewModel.progressDialog.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            if (it) {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager, javaClass.simpleName)
            } else {
                if (progressDialog.isVisible) {
                    progressDialog.dismiss()
                }
            }
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            information = InformationFragment()

            if (it == Cons.DOWNLOAD_RESULT) {
                informationFragment.getInstance(
                    getString(R.string.correct),
                    it
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

    fun clear() {
        viewModel.listAppointmentModel.value = null
        viewModel.progressDialog.value = null
        viewModel.informationFragment.value  = null
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}