package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.databinding.FragmentAdminBinding
import com.uts.homelab.utils.extension.intentToMain
import com.uts.homelab.view.fragment.LoginFragmentDirections
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private lateinit var binding:FragmentAdminBinding

    private val adminViewModel:AdminViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adminViewModel.getTextUI()
        binding.btnExit.setOnClickListener {
            adminViewModel.deleteUserSession()
        }
        observers()

        binding.cardLocation.setOnClickListener{
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToNurseAdminFragment())
        }
        binding.cardNurse.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAddNurseFragment())
        }


        super.onViewCreated(view, savedInstanceState)
    }

    private fun observers() {
        adminViewModel.isUserAuth.observe(viewLifecycleOwner){

            binding.nameAdmin.text = it.name

        }
        adminViewModel.intentToLogin.observe(viewLifecycleOwner) {
            FirebaseAuth.getInstance().signOut()
            intentToMain()
        }
    }
}