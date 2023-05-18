package com.uts.homelab.view.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.uts.homelab.databinding.FragmentRegisterBinding
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null


    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private var valueSpinner = ""
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
            mainViewModel.setRegisterUser(
                arrayOf(
                binding.etName.text.toString(),
                valueSpinner,
                binding.etNumberDocument.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPass.text.toString(),
                binding.etPasswordRetry.text.toString()
            ))
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        mainViewModel.isErrorToast.observe(viewLifecycleOwner){
            val msg: String = when(it){
                -1-> "Tienes Campos Vacios"
                -2 -> "La constrseñas no coinciden"
               else -> ""
           }
            toastMessage(msg)
        }
    }

    private fun spinner() {
        val documents = listOf("CC", "DNI", "TI","DE")
        val adapter = ArrayAdapter(requireContext(),
            R.layout.simple_spinner_item,
            documents
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spTypeDocument.adapter = adapter
        binding.spTypeDocument.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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