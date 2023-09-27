package com.uts.homelab.view.fragment.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentProfileBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.Opinion
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.intentToMain
import com.uts.homelab.viewmodel.ProfileUserViewModel
import java.util.*


class ProfileUserFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel:ProfileUserViewModel  by activityViewModels()
    private lateinit var opinionFragment: OpinionFragment
    private var progressDialog: ProgressFragment = ProgressFragment()

    private var information: InformationFragment? = null
    private val informationFragment by lazy { information!! }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_hospital)

        viewModel.setModel( ProfileUserFragmentArgs.fromBundle(requireArguments()).userModel)

        binding.btnExit.setOnClickListener {
           viewModel.exitUserSession()
        }

        binding.changeLocation.setOnClickListener{
            findNavController().navigate(ProfileUserFragmentDirections.actionNavigationNotificationsToAddressFragment(
                AppointmentUserModel()
            ))
        }
        binding.editData.setOnClickListener{
            findNavController().navigate(ProfileUserFragmentDirections.actionNavigationNotificationsToDataUserProfileFragment())
        }

        binding.mejoras.setOnClickListener{
            getDialogOpinion(Opinion.IMPROVEMENT.name)
        }
        binding.reportProblem.setOnClickListener{
            getDialogOpinion(Opinion.PROBLEM.name)
        }
        binding.changePassword.setOnClickListener {
            viewModel.setRequestChangePassword()
        }
        setObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun getDialogOpinion(type:String){
        opinionFragment = OpinionFragment(type){ message, title ->
            opinionFragment.dismiss()
            viewModel.setMessageOpinion(type,message,title)
        }

        opinionFragment.show(childFragmentManager,"OpinionDialog ${javaClass.name}")
    }



    private fun setObservers() {
        viewModel.modelUser.observe(viewLifecycleOwner){
            binding.nameUser.text = "${it.name} ${it.lastName}"
            binding.ageUser.text = "   |   ${it.age} Años"
        }

        viewModel.exitSession.observe(viewLifecycleOwner){
            intentToMain()
        }

        viewModel.progressDialog.observe(viewLifecycleOwner){
            if(it){
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
                progressDialog.show(childFragmentManager,javaClass.name)
            }else{
                if(progressDialog.isVisible){
                    progressDialog.dismiss()
                }
            }
        }

        viewModel.informationDialog.observe(viewLifecycleOwner){
            if(it==null) return@observe

            information = InformationFragment()
            informationFragment.getInstance(
                "ATENCION ...",
                it
            )


            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment.isVisible) {
                        informationFragment.dismiss()
                    }
                }
            }, 3500)

            informationFragment.show(requireActivity().supportFragmentManager, "InformationFragment")
        }
    }

    private fun clearObservers() {

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }
}