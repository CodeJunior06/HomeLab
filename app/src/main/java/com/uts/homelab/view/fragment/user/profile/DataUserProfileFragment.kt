package com.uts.homelab.view.fragment.user.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentDataUserProfileBinding
import com.uts.homelab.utils.Utils
import com.uts.homelab.viewmodel.ProfileUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DataUserProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var  binding: FragmentDataUserProfileBinding
    private val viewModel:ProfileUserViewModel by activityViewModels()
    private var valueEPS = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataUserProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        spinner()

        viewModel.modelUser.observe(viewLifecycleOwner){
            binding.nameUser.text = it.name + it.lastName
            binding.AgeUser.setText("${it.age} Años")
            binding.DocumentUser.text = it.valueDocument
            binding.emailUser.text = it.email
            binding.nacimientolUser.text = Utils().getCurrentDate(it.nacimiento)
            binding.typeDocumentUser.text = it.typeDocument
            binding.phoneUser.setText(it.phone.toString())
            binding.spTypeIPS.setSelection(0)

        }

        binding.updateData.setOnClickListener {
            viewModel.updateData(arrayOf( valueEPS,binding.phoneUser.text.toString(),binding.AgeUser.text.toString()))
        }
    }

    private fun spinner() {


        val adapterTypeEPS = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            requireActivity().resources.getStringArray(R.array.typeEPS_array)
        )
        adapterTypeEPS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        binding.spTypeIPS.adapter = adapterTypeEPS
        binding.spTypeIPS.onItemSelectedListener = this
    }


    override fun onItemSelected(adapter: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
            valueEPS = adapter.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        println("")
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }
}