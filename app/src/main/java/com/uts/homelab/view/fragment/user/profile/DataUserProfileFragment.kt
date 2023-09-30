package com.uts.homelab.view.fragment.user.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentDataUserProfileBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.viewmodel.ProfileUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DataUserProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var  binding: FragmentDataUserProfileBinding
    private val viewModel:ProfileUserViewModel by activityViewModels()
    private var valueEPS = ""

    private var information: InformationFragment? = null
    private val informationFragment by lazy { information!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataUserProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clear()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        spinner()

        viewModel.modelUser.observe(viewLifecycleOwner){
            binding.nameUser.text = it.name + it.lastName
            binding.AgeUser.setText("${it.age}")
            binding.DocumentUser.text = it.valueDocument
            binding.emailUser.text = it.email
            binding.nacimientolUser.text = Utils().getCurrentDate(it.nacimiento)
            binding.typeDocumentUser.text = it.typeDocument
            binding.phoneUser.setText(it.phone.toString())
            binding.AddressUser.text = it.address
            binding.spTypeIPS.setSelection(0)

        }

        binding.updateData.setOnClickListener {
            viewModel.updateData(arrayOf( valueEPS,binding.phoneUser.text.toString(),binding.AgeUser.text.toString()))
        }

        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        viewModel.informationDialog.observe(viewLifecycleOwner){
            if(it==null) return@observe

            information = InformationFragment()
            if(it == Cons.UPDATE_DATA_USER){
                informationFragment.getInstance(getString(R.string.correct),"Se han actualizado correctamente los datos")
            }else{
                informationFragment.getInstance(getString(R.string.attention), it)
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment.isVisible) {
                        informationFragment.dismiss()
                    }
                }
            }, 3500)

            if(informationFragment.isVisible){
                informationFragment.dismiss()
            }

            informationFragment.show(requireActivity().supportFragmentManager, "InformationFragment")
        }
    }

    private fun clear() {
        viewModel.informationDialog.value = null
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
    }
}