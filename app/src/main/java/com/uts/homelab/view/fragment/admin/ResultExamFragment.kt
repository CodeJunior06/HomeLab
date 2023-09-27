package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentResultExamBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
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
                dialogProgress.show(childFragmentManager,"Progress Fragment ${javaClass.name}")
            }else{
                if(dialogProgress.isVisible){
                    dialogProgress.dismiss()
                }
            }
        }

        viewModel.rvAppointmentUserModel.observe(viewLifecycleOwner){
            if(it == null ) return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
//            binding.rvAppointment.adapter = AdapterNurseAppointment(it)
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

}