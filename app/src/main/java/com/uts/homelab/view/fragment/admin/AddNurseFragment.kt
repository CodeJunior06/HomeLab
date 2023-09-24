package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAddNurseBinding
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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
    private var informationFragment: InformationFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNurseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clear()
                findNavController().popBackStack()
            }
        }


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
                    binding.etName.text.trim().toString(),
                    binding.etLastName.text.trim().toString(),
                    binding.etEmail.text.trim().toString(),
                    binding.etNumberDocument.text.trim().toString(),
                    gender
                )
            )
        }

        setObserver()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBack)


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
            if (it == null) return@observe

            when (it.first) {
                true -> {
                    if (!progressDialog.isVisible) {
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
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }

                    if (it.second == 1) {
                        viewModel.informationFragment.value = getString(R.string.is_nurse_add)
                    }
                }
            }
        }

        viewModel.messageToast.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            toastMessage(getString(R.string.emptyValue))
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            informationFragment = InformationFragment()

            when (it) {
                getString(R.string.is_nurse_add) -> {
                    informationFragment!!.getInstance(
                        getString(R.string.correct),
                        it
                    )
                }
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

                    if (it == getString(R.string.is_nurse_add)) {
                        clear()
                        CoroutineScope(Dispatchers.Main).launch {
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


    fun clear() {
        viewModel.informationFragment.postValue(null)
        viewModel.isProgress.postValue(null)
        viewModel.messageToast.postValue(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clear()
    }
}