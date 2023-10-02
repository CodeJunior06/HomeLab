package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAdminBinding
import com.uts.homelab.network.dataclass.NurseWorkingAdapter
import com.uts.homelab.utils.extension.intentToMain
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminBinding

    private val adminViewModel: AdminViewModel by activityViewModels()
    private var lstWorkingAdapter = arrayListOf<NurseWorkingAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adminViewModel.getTextUI(false)
        adminViewModel.getAllNurseWorkingDay()


        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)

        observers()

        binding.cardLocation.setOnClickListener {
            clear()
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToNurseAdminFragment())
        }
        binding.cardNurse.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAddNurseFragment())
        }
        binding.cardProfile.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAdminProfileFragment())
        }
        binding.cardOnlyLocation.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToSearchNurseFragment())
        }
        binding.cardIncidents.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToPqrsFragment())
        }

        binding.cardLaboratory.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToResultExamFragment())
        }


        super.onViewCreated(view, savedInstanceState)
    }

    private fun observers() {
        adminViewModel.isUserAuth.observe(viewLifecycleOwner) {
            if(it == null) return@observe

            binding.nameAdmin.text = it.name

        }
        adminViewModel.rvNurseWorkingAdapter.observe(viewLifecycleOwner){
            if(it == null) return@observe

            binding.llLoading.visibility = View.GONE
            binding.llNurses.visibility = View.VISIBLE

            lstWorkingAdapter.clear()
            lstWorkingAdapter = it as ArrayList<NurseWorkingAdapter>

            changedCountNurse(lstWorkingAdapter)
            adminViewModel.initAsync()
        }

        adminViewModel.uidChange.observe(viewLifecycleOwner){ workingDayNurse ->
            if(workingDayNurse == null) return@observe

            var idChange = -1
            var isChanged = false
            lstWorkingAdapter.forEachIndexed { index, nurseWorkingAdapter ->
                if(nurseWorkingAdapter.uidNurse == workingDayNurse.id){
                    if(workingDayNurse.active != nurseWorkingAdapter.active){
                        isChanged = true
                        idChange = index
                        return@forEachIndexed
                    }
                }
            }
            if(isChanged){
                lstWorkingAdapter[idChange].active = workingDayNurse.active
                changedCountNurse(lstWorkingAdapter)
            }
        }
    }

    private fun changedCountNurse(lst:List<NurseWorkingAdapter>){
        var lstOn = 0
        var lstOff = 0

        lst.forEach {
            if(it.active){
                lstOn++
            }else{
                lstOff++
            }

        }

        binding.numberNurse.text = "${lst.size} Enfermeros"
        binding.nurseOn.text = "${lstOn} Enfermeros"
        binding.nurseOff.text = "${lstOff} Enfermeros"
    }

    private fun clear(){
        adminViewModel.isUserAuth.value = null
        adminViewModel.rvNurseWorkingAdapter.value = null
        adminViewModel.uidChange.value = null
        adminViewModel.stopAsync()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clear()

    }
}