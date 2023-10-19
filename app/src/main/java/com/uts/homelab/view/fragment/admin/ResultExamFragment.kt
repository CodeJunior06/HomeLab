package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentResultExamBinding
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterUserResult
import com.uts.homelab.viewmodel.AdminViewModel
import java.util.*


class ResultExamFragment : Fragment() {
    private lateinit var binding:FragmentResultExamBinding
    private val viewModel:AdminViewModel by activityViewModels()

    private val dialogProgress = ProgressFragment()
    private var informationFragment:InformationFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultExamBinding.inflate(inflater,container,false)
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

        viewModel.getAppointmentLaboratory()
        setObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        viewModel.isProgress.observe(viewLifecycleOwner){
            if(it == null ) return@observe

            if(it.first){
                if(dialogProgress.isVisible){
                    dialogProgress.dismiss()
                }
                dialogProgress.show(childFragmentManager,"Progress Fragment ${javaClass.simpleName}")
            }else{
                if(dialogProgress.isVisible){
                    dialogProgress.dismiss()
                }
            }
        }

        viewModel.rvAppointmentUserModel.observe(viewLifecycleOwner){
            if(it == null ) return@observe

            binding.countPending.text = it.size.toString()
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter = AdapterUserResult(it,Rol.ADMIN){
                viewModel.sendResult(it)
            }
        }

        viewModel.informationFragment.observe(viewLifecycleOwner){
            if (it == null) return@observe

            informationFragment = InformationFragment()

            when (it) {
                else -> {
                    informationFragment!!.getInstance(
                        getString(R.string.attention),
                        it
                    )
                }
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment!!.isVisible || informationFragment!!.isAdded) {
                        informationFragment!!.dismiss()
                    }

                }
            }, 3000)

            informationFragment!!.show(
                childFragmentManager,
                "InformationFragment"
            )
        }
    }
    fun clear(){
     viewModel.rvAppointmentUserModel.value = null
     viewModel.informationFragment.value = null
     viewModel.isProgress.value =null
    }

    override fun onDestroy() {
        super.onDestroy()
        clear()
    }

}