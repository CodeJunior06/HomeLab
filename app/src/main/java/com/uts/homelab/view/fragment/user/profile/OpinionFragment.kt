package com.uts.homelab.view.fragment.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.uts.homelab.databinding.FragmentOpinionBinding

class OpinionFragment(private val type:String,private val onCall:(message:String)->Unit ) : DialogFragment() {

    private lateinit var binding:FragmentOpinionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpinionBinding.inflate(layoutInflater)

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setCancelable(false)


        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.typeComentary.text = if(type.equals("Problem")){
            "PROBLEMA"
        }else{
            "MEJORA U OBSERVACION"
        }
       binding.btnCancel.setOnClickListener {
           dismiss()
       }

        binding.btnSend.setOnClickListener {
            if(binding.editTextTextMultiLine.text.isEmpty()){
                Toast.makeText(requireContext(),"No has llenado la caja de comentarios",Toast.LENGTH_LONG).show()
            }else{
                onCall(binding.editTextTextMultiLine.text.toString())
            }
        }

    }

}