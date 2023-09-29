package com.uts.homelab.view.fragment.nurse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentNurseBinding
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.TypeView
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.view.adapter.AdapterUserAppointment
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


        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        viewModel.init()
        observers()

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(NurseFragmentDirections.actionNurseFragmentToNurseProfile())
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(NurseFragmentDirections.actionNurseFragmentToNurseHistory())
        }
    }

    private fun observers() {
        viewModel.nurseModel.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            binding.nameNurse.text = it.name
             this.navDirections = NurseFragmentDirections.actionNurseFragmentToNurseDataFragment(it)
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if(it== null) return@observe

            informationDialog = InformationFragment()
            if (it == Cons.VIEW_DIALOG_INFORMATION) {
                informationDialog.getInstance(
                    getString(R.string.attention),
                    "Hemos detectado que faltan datos para continuar con el procesos",
                    "Ir a llenar"
                ) {
                    findNavController().navigate(navDirections!!)
                    informationDialog.dismissNow()
                }
            } else {
                informationDialog.getInstance("ATENCION", it)
            }

            if (!informationDialog.isAdded) {
                informationDialog.show(requireActivity().supportFragmentManager, "gg")
            }
        }
        viewModel.progressDialogRv.observe(viewLifecycleOwner){
            if (it.first) {

                if(it.second==2 || it.second == 3){
                    binding.loading.visibility = View.VISIBLE
                    binding.rvAppointment.visibility = View.GONE
                    binding.messageLoading.setText("CARGANDO DISPONIBILIDAD . . .")
                    if(it.second == 3){
                        binding.messageLoading.setText("No se encuentran tareas para hoy")
                    }
                }else{

                }


            } else {
                if(it.second==2){
                    binding.loading.visibility = View.GONE
                    binding.rvAppointment.visibility = View.VISIBLE
                }else{

                }

            }
        }

        viewModel.setRecycler.observe(viewLifecycleOwner){
            if(it ==null ) return@observe
            binding.rvAppointment.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAppointment.adapter = AdapterUserAppointment(it, TypeView.MAIN,
                AdapterUserAppointment.VIEW_NURSE)
            binding.countVisit.text = it.size.toString()
            if(it.isEmpty()){
                viewModel.progressDialogRv.postValue(Pair(true,3))
            }else{
                binding.loading.visibility = View.GONE
                binding.rvAppointment.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.informationFragment.value = null
    }
}