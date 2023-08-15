package com.uts.homelab.view.fragment.nurse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.databinding.FragmentNurseProfileBinding
import com.uts.homelab.viewmodel.NurseViewModel


class NurseProfileFragment : Fragment() {

    private lateinit var binding: FragmentNurseProfileBinding
    private val viewModel:NurseViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNurseProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            viewModel.deleteNurseSession()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}