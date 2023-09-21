package com.uts.homelab.view.fragment.nurse

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.databinding.FragmentNurseProfileBinding
import com.uts.homelab.network.LocationService
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.viewmodel.NurseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.security.Provider.Service

@AndroidEntryPoint
class NurseProfileFragment : Fragment() {

    private lateinit var binding: FragmentNurseProfileBinding
    private val viewModel:NurseViewModel by activityViewModels()

    private val progressDialog:ProgressFragment = ProgressFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNurseProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.getJournal()

        binding.btnExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            viewModel.deleteNurseSession()
        }

        viewModel.modelWorkingDay.observe(viewLifecycleOwner){
            if(it.active){
                viewModel.isService.postValue(true)
            }else{
                binding.state.text = "DESACTIVADO"
                binding.btnJornaly.text = "INICAR JORNADA"
            }
        }

        binding.btnJornaly.setOnClickListener {

            if(binding.btnJornaly.text.toString().equals("FINALIZAR JORNADA")){
                viewModel.stopJournal()
            }else {
                viewModel.initJournal()
            }
        }

        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setObserver() {
        viewModel.progressDialog.observe(viewLifecycleOwner){

            if(it){
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager,"Progress Dialog ${javaClass.name}")
            }else{
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
            }

        }
        viewModel.isService.observe(viewLifecycleOwner){
            if (it){
                binding.state.text = "ACTIVADO"
                binding.btnJornaly.text = "FINALIZAR JORNADA"

                val serviceIntent = Intent(requireContext(), LocationService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(serviceIntent)
                }else{
                    requireActivity().startService(serviceIntent)
                }
            }else{
                binding.state.text = "DESACTIVADO"
                binding.btnJornaly.text = "INICAR JORNADA"
                val serviceIntent = Intent(requireContext(), LocationService::class.java)
                serviceIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireActivity().stopService(serviceIntent)
            }

        }

    }
}