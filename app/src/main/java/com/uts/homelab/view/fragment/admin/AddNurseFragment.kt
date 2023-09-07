package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAddNurseBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddNurseFragment : Fragment() {

    private lateinit var binding: FragmentAddNurseBinding

    private var gender: String? = null
    private var boolGender: Int = 0

    private val viewModel: AdminViewModel by activityViewModels()

    private var progressDialog: ProgressFragment = ProgressFragment()
    private  var informationFragment: InformationFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNurseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.cardNurseMen.setOnClickListener {
            if (gender.isNullOrEmpty()) {
                boolGender = 1
                gender = "M"
            } else {
                if (boolGender == 2) {
                    boolGender = 1
                    gender = "M"

                }
            }
            binding.cardNurseMen.alpha = 1.0f
            binding.cardNurseWomen.alpha = 0.5f
        }
        binding.cardNurseWomen.setOnClickListener {
            if (gender.isNullOrEmpty()) {
                boolGender = 2
                gender = "F"
            } else {
                if (boolGender == 1) {
                    boolGender = 2
                    gender = "F"

                }
            }
            binding.cardNurseWomen.alpha = 1.0f
            binding.cardNurseMen.alpha = 0.5f
        }
        binding.btnRegresar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRegister.setOnClickListener {
            viewModel.insertNurse(
                arrayOf(
                    binding.etName.text.toString(),
                    binding.etLastName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etNumberDocument.text.toString(),
                    gender
                )
            )
        }

        setObserver()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun generateMessage(second: Int): String {
        return when (second) {
            1 -> getString(R.string.register_auth)
            2 -> getString(R.string.register_user_firestore)
            else -> ""
        }
    }

    private fun setObserver() {
        viewModel.isProgress.observe(viewLifecycleOwner) {

            when (it.first) {
                true -> {
                    if (!progressDialog.isVisible && !progressDialog.isStateSaved) {
                        progressDialog = ProgressFragment.getInstance(generateMessage(it.second))
                        progressDialog.show(
                            requireActivity().supportFragmentManager,
                            "ProgressDialog"
                        )
                    } else {
                        progressDialog.updateMessage(generateMessage(it.second))
                    }


                }
                false -> {
                    if(it.second ==-1) return@observe
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }
                    if (it.second == 1) {
                        viewModel.informationFragment.postValue(getString(R.string.is_user_add))
                    }
                }
            }
        }

        viewModel.messageToast.observe(viewLifecycleOwner) {
            toastMessage(getString(R.string.emptyValue))
        }
        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if(it == null) return@observe

            informationFragment = InformationFragment()

            when (it) {
                getString(R.string.is_user_add) -> {
                    informationFragment!!.getInstance(
                        "EXITO",
                        it
                    )
                }
                else -> {
                    informationFragment!!.getInstance(
                        "ATENCION",
                        it
                    )
                }
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment!!.isVisible || informationFragment!!.isAdded ) {
                        informationFragment!!.dismiss()
                    }

                    if (it == getString(R.string.is_user_add)) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            informationFragment = null
                            childFragmentManager.executePendingTransactions()
                            findNavController().popBackStack()
                        }
                    }
                }
            }, 3000)

            informationFragment!!.showNow(
                childFragmentManager,
                "InformationFragment"
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        toastMessage("DESTORY ${javaClass.name}")
        viewModel.informationFragment.value = null
        viewModel.isProgress.value = Pair(false,-1)
    }
}