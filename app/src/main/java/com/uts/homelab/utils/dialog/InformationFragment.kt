package com.uts.homelab.utils.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.uts.homelab.databinding.FragmentInformationBinding
import javax.inject.Singleton

@Singleton
class InformationFragment: DialogFragment() {
    private lateinit var dialogBinding: FragmentInformationBinding

    companion object {

        private var INSTANCE: InformationFragment? = null
        private lateinit var title:String
        private  lateinit var message:String
        private  lateinit var btnDoneMessage:String
        private  lateinit var btnCancelMessage:String
        private var isButtons = false
        private var isOnlyButton = false
        private lateinit var callback: (op: Int) -> Unit


        fun getInstance(title:String, message:String): InformationFragment {
            this.title = title
            this.message = message
            isButtons = false
            if (INSTANCE == null) {
                INSTANCE = InformationFragment()
            }
            return INSTANCE!!
        }

        fun getInstance(title:String, message:String, btnDone:String, btnCancel:String, buttons:Boolean, callback:(op:Int)->Unit): InformationFragment {
            this.title = title
            this.message = message
            this.btnDoneMessage = btnDone
            this.btnCancelMessage = btnCancel
            this.isButtons = buttons
            this.callback = callback
            if (INSTANCE == null) {
                INSTANCE = InformationFragment()
            }
            return INSTANCE!!
        }
        fun getInstance(title:String, message:String, btnDone:String, callback:(op:Int)->Unit): InformationFragment {
            this.title = title
            this.message = message
            this.btnDoneMessage = btnDone
            this.isOnlyButton = true
            this.callback = callback
            if (INSTANCE == null) {
                INSTANCE = InformationFragment()
            }
            return INSTANCE!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dialogBinding = FragmentInformationBinding.inflate(layoutInflater)

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
        return dialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogBinding.tvTittle.text = title
        dialogBinding.tvMessage.text = message

        if(isButtons){
            dialogBinding.btnExit.text = btnCancelMessage
            dialogBinding.btnCancel.text = btnDoneMessage
            dialogBinding.llButtons.visibility = View.VISIBLE
        }else{
            dialogBinding.llButtons.visibility = View.GONE
        }

        if(isOnlyButton){
            dialogBinding.btnOnlyAction.text = btnDoneMessage
            dialogBinding.llOnlyButton.visibility = View.VISIBLE
        }else{
            dialogBinding.llOnlyButton.visibility = View.GONE
        }

        dialogBinding.btnOnlyAction.setOnClickListener {
            callback(0)
        }
        dialogBinding.btnCancel.setOnClickListener {
            callback(0)
        }
        dialogBinding.btnExit.setOnClickListener {
            callback(1)
        }
    }
}