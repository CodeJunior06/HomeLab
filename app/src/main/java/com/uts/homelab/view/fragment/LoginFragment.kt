package com.uts.homelab.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentLoginBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.intentToUserHome
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.MainViewModel
import java.util.*

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var progressDialog: ProgressFragment = ProgressFragment()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var informationDialog: InformationFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegistrar.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnIngresar.setOnClickListener {
            mainViewModel.setLoginUser(
                binding.etName.text.toString(),
                binding.etPass.text.toString()
            )
        }

        observers()
    }

    private fun observers() {
        mainViewModel.isProgress.observe(viewLifecycleOwner) {
            when (it.first) {
                true -> {
                    if (!progressDialog.isVisible && !progressDialog.isStateSaved) {
                        progressDialog = ProgressFragment.getInstance(generateMessage(it.second))
                        progressDialog.show(
                            requireActivity().supportFragmentManager,
                            "ProgressDialog"
                        )
                    } else {
                        progressDialog.updateMessage(generateMessage(it.second))
                    }

                }
                false -> {
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }
                    when (it.second) {
                        1 -> {
                            intentToUserHome()
                        }
                        2 -> {
                            findNavController().navigate(R.id.action_registerFragment_to_nav_admin)
                        }
                        3 -> {
                            findNavController().navigate(R.id.action_registerFragment_to_nav_nurse)
                        }
                    }
                }
            }
        }

        mainViewModel.isErrorToast.observe(viewLifecycleOwner) {

            val msg: String = when (it) {
                -1 -> getString(R.string.emptyEmail)
                -2 -> getString(R.string.emptyPassword)
                else -> ""
            }
            if (msg.isNotEmpty()) toastMessage(msg)
        }

        mainViewModel.informationFragment.observe(viewLifecycleOwner) {
            informationDialog = InformationFragment.getInstance(
                "ATENCION ...",
                it
            )


            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationDialog.isVisible) {
                        informationDialog.dismiss()
                    }
                }
            }, 3500)
            informationDialog.show(requireActivity().supportFragmentManager, "InformationFragment")
        }

        mainViewModel.intentToLogin.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

    private fun generateMessage(second: Int): String {
        return when (second) {
            1 -> getString(R.string.register_auth)
            2 -> getString(R.string.get_data_user_auth)
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}