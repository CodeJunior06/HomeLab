package com.uts.homelab.view.fragment.nurse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentNurseHistoryBinding
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State
import com.uts.homelab.view.adapter.AdapterHistoryAppointment
import com.uts.homelab.viewmodel.NurseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NurseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentNurseHistoryBinding
    private val viewModel: NurseViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNurseHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getAllAppointment()
        seeObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun seeObserver() {
        viewModel.setRecycler.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter =
                AdapterHistoryAppointment(it, Rol.NURSE) { _, _ ->

                }
        }

        viewModel.progressDialog.observe(viewLifecycleOwner){

        }

        viewModel.informationFragment.observe(viewLifecycleOwner){

        }
    }

}