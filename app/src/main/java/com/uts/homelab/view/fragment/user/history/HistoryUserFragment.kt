package com.uts.homelab.view.fragment.user.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.databinding.FragmentHistoryBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.TypeView
import com.uts.homelab.view.adapter.AdapterUserAppointment
import com.uts.homelab.viewmodel.UserViewModel
import java.util.*

class HistoryUserFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    private var progressDialog: ProgressFragment = ProgressFragment()
    private var informationDialog: InformationFragment = InformationFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllAppointment()
        setObserver()
    }

    private fun clearObsrvers(){
        viewModel.informationFragment.value = null
        viewModel.listAppointmentModel.value = null
    }
    private fun setObserver() {
        viewModel.informationFragment.observe(viewLifecycleOwner){
            if(it == null) return@observe
            informationDialog = InformationFragment()
            informationDialog.getInstance(
                "ATENCION ...",
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

        viewModel.listAppointmentModel.observe(viewLifecycleOwner){
            if(it == null) return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter = AdapterUserAppointment(it, TypeView.HISTORY)

        }

        viewModel.progressDialog.observe(viewLifecycleOwner){
            if(it){
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager,javaClass.name)
            }else{
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearObsrvers()
        _binding = null
    }
}