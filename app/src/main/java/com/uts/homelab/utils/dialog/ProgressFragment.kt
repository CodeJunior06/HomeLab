package com.uts.homelab.utils.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentProgressBinding
import javax.inject.Singleton


@Singleton
class ProgressFragment: DialogFragment() {
      private var dialogBinding:FragmentProgressBinding? = null


    companion object {
        private var INSTANCE: ProgressFragment? = null
        private var message:String =""

        fun getInstance(message:String): ProgressFragment {
            this.message = message
            if (INSTANCE == null) {
                INSTANCE = ProgressFragment()
            }
            return INSTANCE!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dialogBinding = FragmentProgressBinding.inflate(layoutInflater)

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
        return dialogBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(message.isNotEmpty()){
            dialogBinding!!.textDialog.text = message
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun updateMessage(generateMessage: String) {
        dialogBinding!!.textDialog.text = generateMessage
    }

}