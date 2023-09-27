package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentPqrsBinding
import com.uts.homelab.utils.Opinion
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterPQRS
import com.uts.homelab.viewmodel.AdminViewModel
import java.util.*


class PqrsFragment : Fragment() {
    private lateinit var binding: FragmentPqrsBinding
    private val viewModel: AdminViewModel by activityViewModels()
    private var progressDialog = ProgressFragment()
    private var informationFragment: InformationFragment? = null

    private var typeRol: String? = null
    private var boolTypeRol: Int = 0
    private var typeOpinion: String? = null
    private var boolTypeOpinion: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPqrsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getAllPQRS()
        setObserver()


        binding.cardNurse.setOnClickListener {
            if (typeRol.isNullOrEmpty()) {
                boolTypeRol = 1
                typeRol = Rol.NURSE.name
            } else {
                if (boolTypeRol == 2) {
                    boolTypeRol = 1
                    typeRol = Rol.NURSE.name

                }
            }
            binding.cardNurse.alpha = 1.0f
            binding.cardUser.alpha = 0.5f
        }
        binding.cardUser.setOnClickListener {
            if (typeRol.isNullOrEmpty()) {
                boolTypeRol = 2
                typeRol = Rol.USER.name
            } else {
                if (boolTypeRol == 1) {
                    boolTypeRol = 2
                    typeRol =Rol.USER.name

                }
            }
            binding.cardUser.alpha = 1.0f
            binding.cardNurse.alpha = 0.5f
        }

        binding.itemProblem.setOnClickListener {
            if (typeOpinion.isNullOrEmpty()) {
                boolTypeOpinion = 1
                typeOpinion = Opinion.PROBLEM.name
            } else {
                if (boolTypeOpinion == 2 || boolTypeOpinion == 3) {
                    boolTypeOpinion = 1
                    typeOpinion = Opinion.PROBLEM.name

                }
            }
            binding.itemProblem.alpha = 1.0f
            binding.itemImprovement.alpha = 0.5f
            binding.itemProblemAppointment.alpha = 0.5f
        }

        binding.itemImprovement.setOnClickListener {
            if (typeOpinion.isNullOrEmpty()) {
                boolTypeOpinion = 2
                typeOpinion = Opinion.IMPROVEMENT.name
            } else {
                if (boolTypeOpinion == 1 || boolTypeOpinion == 3) {
                    boolTypeOpinion = 2
                    typeOpinion = Opinion.IMPROVEMENT.name
                }
            }
            binding.itemImprovement.alpha = 1.0f
            binding.itemProblem.alpha = 0.5f
            binding.itemProblemAppointment.alpha = 0.5f
        }

        binding.itemProblemAppointment.setOnClickListener {
            if (typeOpinion.isNullOrEmpty()) {
                boolTypeOpinion = 3
                typeOpinion = Opinion.PROBLEMAPPOINTMENT.name
            } else {
                if (boolTypeOpinion == 1 || boolTypeOpinion == 2) {
                    boolTypeOpinion = 3
                    typeOpinion = Opinion.PROBLEMAPPOINTMENT.name
                }
            }
            binding.itemProblemAppointment.alpha = 1.0f
            binding.itemProblem.alpha = 0.5f
            binding.itemImprovement.alpha = 0.5f
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setObserver() {
        viewModel.isProgress.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            when (it.first) {
                true -> {
                    if (progressDialog.isVisible) {

                        progressDialog.dismiss()
                    }
                    progressDialog.show(
                        requireActivity().supportFragmentManager,
                        "ProgressDialog"
                    )
                }
                false -> {
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }

                }
            }
        }
        viewModel.informationFragment.observe(viewLifecycleOwner) {
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

            informationFragment!!.showNow(
                childFragmentManager,
                "InformationFragment"
            )
        }

        viewModel.rvCommentType.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.rvCommentType.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCommentType.adapter = AdapterPQRS(it)
        }


    }

    private fun clear() {
        TODO("Not yet implemented")
    }


}