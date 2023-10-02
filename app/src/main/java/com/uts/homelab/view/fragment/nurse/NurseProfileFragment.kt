package com.uts.homelab.view.fragment.nurse

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentNurseProfileBinding
import com.uts.homelab.network.LocationService
import com.uts.homelab.utils.Cons
import com.uts.homelab.utils.Opinion
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.intentToMain
import com.uts.homelab.view.fragment.user.profile.OpinionFragment
import com.uts.homelab.viewmodel.NurseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.security.Provider.Service
import java.util.*

@AndroidEntryPoint
class NurseProfileFragment : Fragment() {

    private lateinit var binding: FragmentNurseProfileBinding
    private val viewModel:NurseViewModel by activityViewModels()

    private val progressDialog:ProgressFragment = ProgressFragment()

    private var information: InformationFragment? = null
    private val informationFragment by lazy { information!! }

    private lateinit var opinionFragment: OpinionFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNurseProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        viewModel.getJournal()

        binding.btnJornaly.setOnClickListener {

            if(binding.btnJornaly.text.toString().equals("FINALIZAR JORNADA")){
                viewModel.stopJournal()
            }else {
                viewModel.initJournal()
            }
        }

        binding.editData.setOnClickListener {
          findNavController().navigate(R.id.action_nurse_profile_to_dataNurseProfileFragment)
        }

        binding.changePassword.setOnClickListener {
            viewModel.changePassword()
        }

        binding.reportProblem.setOnClickListener {
            opinionFragment = OpinionFragment(Opinion.PROBLEM.name){ message, title ->
                opinionFragment.dismiss()
                viewModel.setMessageOpinion(Opinion.PROBLEM.name,message,title)
            }

            opinionFragment.show(childFragmentManager,"OpinionDialog ${javaClass.name}")

        }

        binding.btnExit.setOnClickListener {
            viewModel.deleteNurseSession()
        }

        setObserver()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setObserver() {

        viewModel.intent.observe(viewLifecycleOwner){
            if(it == null) return@observe

            val serviceIntent = Intent(requireContext(), LocationService::class.java)
            serviceIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            requireActivity().stopService(serviceIntent)

            intentToMain()
        }

        viewModel.progressDialog.observe(viewLifecycleOwner){
            if(it == null)return@observe

            if(it){
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager,"Progress Dialog ${javaClass.name}")
            }else{
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
            }

        }
        viewModel.isService.observe(viewLifecycleOwner){
            if(it == null) return@observe

            if (it){
                binding.state.text = "ACTIVADO"
                binding.state.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

                binding.btnJornaly.text = "FINALIZAR JORNADA"

                val serviceIntent = Intent(requireContext(), LocationService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(serviceIntent)
                }else{
                    requireActivity().startService(serviceIntent)
                }
            }else{
                binding.state.text = "FUERA DE SERVICIO"
                binding.state.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_light))
                binding.btnJornaly.text = "INICAR JORNADA"
                val serviceIntent = Intent(requireContext(), LocationService::class.java)
                serviceIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireActivity().stopService(serviceIntent)
            }

        }

        viewModel.nurseModel.observe(viewLifecycleOwner){
            if(it == null) return@observe

            binding.nameNurse.text = "${it.name} ${it.lastName}"
            binding.expNurse.text = "${it.exp} Meses"
        }
        viewModel.modelWorkingDay.observe(viewLifecycleOwner){
            if(it == null) return@observe

            if(it.active){
                viewModel.isService.postValue(true)
            }else{
                binding.state.text = "FUERA DE SERVICIO"
                binding.btnJornaly.text = "INICAR JORNADA"
                binding.state.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_light))
            }
        }


        viewModel.informationFragment.observe(viewLifecycleOwner){
            if(it==null) return@observe

            information = InformationFragment()
            if(it == Cons.UPDATE_PASSWORD){
                informationFragment.getInstance(getString(R.string.correct),"Se ha enviado un correo para la continuacion del restablecimiento de contraseña")
            }else{
                informationFragment.getInstance(getString(R.string.attention), it)
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment.isVisible) {
                        informationFragment.dismiss()
                    }
                }
            }, 3500)

            if(informationFragment.isVisible){
                informationFragment.dismiss()
            }

            informationFragment.show(requireActivity().supportFragmentManager, "InformationFragment")
        }

    }

    fun clear(){
        viewModel.intent.value = null
        viewModel.informationFragment.value = null
        viewModel.progressDialog.value = null
        viewModel.isService.value = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clear()
    }
}