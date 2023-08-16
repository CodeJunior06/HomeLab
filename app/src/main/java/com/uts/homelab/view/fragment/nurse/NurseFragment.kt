package com.uts.homelab.view.fragment.nurse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.uts.homelab.databinding.FragmentNurseBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.view.NurseActivity
import com.uts.homelab.viewmodel.NurseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NurseFragment : Fragment() {

    private lateinit var binding: FragmentNurseBinding
    private val viewModel: NurseViewModel by activityViewModels()
    private var navDirections:NavDirections? =null

    private lateinit var informationDialog: InformationFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNurseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
        observers()
    }

    private fun observers() {
        viewModel.nurseModel.observe(viewLifecycleOwner) {
            binding.nameNurse.text = it.name!!
             this.navDirections = NurseFragmentDirections.actionNurseFragmentToNurseDataFragment(it)
        }

        viewModel.informationFragmentFragment.observe(viewLifecycleOwner) {
            if(it== null) return@observe
            println("ENTROOOOOOOOO!!! $it")

            informationDialog = if (it == Cons.VIEW_DIALOG_INFORMATION) {
                InformationFragment.getInstance(
                    "ATENCION",
                    "Hemos detectado que faltan datos para continuar con el procesos",
                    "Ir a llenar"
                ) {
                    val act = requireActivity() as NurseActivity
                    act.isViewBottomNavigation(false)
                    findNavController().navigate(navDirections!!)
                    informationDialog.dismissNow()
                }
            } else {
                InformationFragment.getInstance("ATENCION", it)
            }

            if (!informationDialog.isAdded) {
                informationDialog.show(requireActivity().supportFragmentManager, "gg")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.informationFragmentFragment.value = null
    }
}