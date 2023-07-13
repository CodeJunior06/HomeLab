package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAddNurseBinding

class AddNurseFragment : Fragment() {

    private lateinit var binding:FragmentAddNurseBinding
    private var gender:String? = null
    private var boolGender:Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNurseBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.cardNurseMen.setOnClickListener {
            if(gender.isNullOrEmpty()){
                boolGender = 1
                gender = "M"
            }else{
                if(boolGender == 2){
                    boolGender = 1
                    gender = "M"

                }
                binding.cardNurseMen.alpha = 1.0f
                binding.cardNurseWomen.alpha = 0.5f
            }
        }
        binding.cardNurseWomen.setOnClickListener {
            if(gender.isNullOrEmpty()){
                boolGender = 2
                gender = "F"
            }else{
                if(boolGender == 1){
                    boolGender = 2
                    gender = "F"

                }
                binding.cardNurseWomen.alpha = 1.0f
                binding.cardNurseMen.alpha = 0.5f
            }
        }
        binding.btnRegresar.setOnClickListener {
            findNavController().popBackStack()
        }

        super.onViewCreated(view, savedInstanceState)
    }

}