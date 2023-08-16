package com.uts.homelab.view.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentRegisterBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null


    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private var valueSpinner = ""
    private var progressDialog: ProgressFragment = ProgressFragment()
    private lateinit var informationDialog: InformationFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        spinner()
        setObserver()

        binding.btnRegister.setOnClickListener {
            mainViewModel.setRegisterUserAuth(
                arrayOf(
                    binding.etName.text.toString(),
                    valueSpinner,
                    binding.etNumberDocument.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPass.text.toString(),
                    binding.etPasswordRetry.text.toString()
                )
            )
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        mainViewModel.isErrorToast.observe(viewLifecycleOwner) {
            val msg: String = when (it) {
                -1 -> getString(R.string.emptyValue)
                -2 -> getString(R.string.password_not_equals)
                else -> ""
            }
            if (msg.isNotEmpty()) toastMessage(msg)

        }

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
                    if (it.second == 1) {
                        mainViewModel.informationFragment.postValue(getString(R.string.is_user_add))
                    }
                }
            }
        }

        mainViewModel.informationFragment.observe(viewLifecycleOwner) {

            informationDialog = when (it) {
                getString(R.string.is_user_add) -> {
                    InformationFragment.getInstance(
                        "EXITO",
                        it
                    )
                }
                else -> {
                    InformationFragment.getInstance(
                        "ATENCION",
                        it
                    )
                }
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationDialog.isVisible) {
                        informationDialog.dismiss()
                    }
                    if (it == getString(R.string.is_user_add)) {
                        mainViewModel.intentToLogin.postValue(Unit)
                    }
                }
            }, 3000)
            informationDialog.show(requireActivity().supportFragmentManager, "InformationFragment")
        }

        mainViewModel.intentToLogin.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.btnRegresar.setOnClickListener {
            mainViewModel.intentToLogin.value = Unit
        }
    }

    private fun generateMessage(second: Int): String {
        return when (second) {
            1 -> getString(R.string.register_auth)
            2 -> getString(R.string.register_user_firestore)
            else -> ""
        }
    }

    private fun spinner() {
        val documents = listOf("CC", "DNI", "TI", "DE")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            documents
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTypeDocument.adapter = adapter
        binding.spTypeDocument.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    valueSpinner = documents[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    println("")
                }

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}