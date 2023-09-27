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
import com.uts.homelab.utils.Opinion

class OpinionFragment(
    private val type: String,
    private val onCall: (message: String, title: String) -> Unit
) : DialogFragment() {

    private lateinit var binding: FragmentOpinionBinding

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

        if (type == Opinion.PROBLEM.name) {
            binding.typeComentary.text = "PROBLEMA"
        } else {
            binding.typeComentary.text = "MEJORA U OBSERVACION"
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSend.setOnClickListener {
            if (binding.editTextTextMultiLine.text.isEmpty() || binding.title.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "No has llenado la caja de comentarios o el titulo",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                onCall(binding.editTextTextMultiLine.text.toString(), binding.title.text.toString())
            }
        }

    }

}