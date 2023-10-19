package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentPqrsBinding
import com.uts.homelab.network.dataclass.CommentType
import com.uts.homelab.utils.Opinion
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterPQRS
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PqrsFragment : Fragment() {
    private lateinit var binding: FragmentPqrsBinding
    private val viewModel: AdminViewModel by activityViewModels()
    private var progressDialog = ProgressFragment()
    private var informationFragment: InformationFragment? = null

    private var typeRol: String? = null
    private var boolTypeRol: Int = 0
    private var typeOpinion: String? = null
    private var boolTypeOpinion: Int = 0

    private var listAllCommentType = listOf<CommentType>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPqrsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            clear()
            findNavController().popBackStack()
        }

        viewModel.getAllPQRS()
        seeObserver()


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
            changeRv()
        }
        binding.cardUser.setOnClickListener {
            if (typeRol.isNullOrEmpty()) {
                boolTypeRol = 2
                typeRol = Rol.USER.name
            } else {
                if (boolTypeRol == 1) {
                    boolTypeRol = 2
                    typeRol = Rol.USER.name

                }
            }
            binding.cardUser.alpha = 1.0f
            binding.cardNurse.alpha = 0.5f
            changeRv()
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

            changeRv()
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
            changeRv()
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
            changeRv()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun changeRv() {
        val lstTemporal = arrayListOf<CommentType>()
        viewModel.lstAllCommentType.value!!.forEach {

            if (typeRol == null) {
                if (it.type == (typeOpinion ?: "")) {
                    lstTemporal.add(it)
                }
            } else if (typeOpinion == null) {
                if (it.rol == (typeRol ?: "")) {
                    lstTemporal.add(it)
                }
            } else {
                if (it.rol == (typeRol ?: "") && it.type == (typeOpinion ?: "")) {
                    lstTemporal.add(it)
                }
            }


        }
        viewModel.rvCommentType.value = lstTemporal
    }

    private fun seeObserver() {
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
        viewModel.isProgress.value = null
        viewModel.rvCommentType.value = null
        viewModel.informationFragment.value = null
        viewModel.lstAllCommentType.value = null
    }


}