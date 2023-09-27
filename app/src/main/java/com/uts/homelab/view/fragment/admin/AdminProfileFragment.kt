package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAdminProfileBinding
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.intentToMain
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminProfileFragment : Fragment() {

    private lateinit var binding:FragmentAdminProfileBinding
    private val adminViewModel: AdminViewModel by activityViewModels()

    private val progressDialog = ProgressFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        adminViewModel.getProfileInfo()

        binding.btnExit.setOnClickListener {
            adminViewModel.deleteUserSession()
        }

        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        adminViewModel.intentToLogin.observe(viewLifecycleOwner) {
            FirebaseAuth.getInstance().signOut()
            intentToMain()
        }
        adminViewModel.isUserAuth.observe(viewLifecycleOwner){
            if(it == null) return@observe
            binding.ip.text = it.ip
            binding.lastDateOnline.text = it.lastDate
            binding.lastTimeOnline.text = it.lastHour
            binding.phone.text = it.phone
            binding.nameAdmin.text = it.name
        }

        adminViewModel.isProgress.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            when (it.first) {
                true -> {
                    if (progressDialog.isVisible) {

                        progressDialog.dismiss()
                    }
                    progressDialog.show(
                        requireActivity().supportFragmentManager,
                        "ProgressDialog"
                    )
                }
                false -> {
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        adminViewModel.isProgress.value = null
    }

}