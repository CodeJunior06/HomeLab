package com.uts.homelab.view.fragment.user.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentAppointmentUserSecondScreenBinding
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.utils.Utils
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.view.adapter.AdapterNurseAvailable
import com.uts.homelab.viewmodel.userViewmodel.AppointmentUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AppointmentUserSecondScreenFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentAppointmentUserSecondScreenBinding? = null
    private val viewModel: AppointmentUserViewModel by activityViewModels()
    private val binding get() = _binding!!

    private var valueSpinner = ""
    private var nurseSelect: NurseRegister? = null

    private var informationDialog: InformationFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAppointmentUserSecondScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnAddAppointment.setOnClickListener {
            viewModel.sendModel()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonYes -> {
                    binding.btnAddAppointment.text = "Agendar Cita"
                }
                R.id.radioButtonNo -> {
                    binding.btnAddAppointment.text = "Seleccionar Direccion"
                }
            }
        }
        return root
    }

    private var progressDialog: ProgressFragment = ProgressFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinner()

        binding.tvDateSelected.setOnClickListener {
            showDatePickerDialog()
        }

        setObserver()
    }

    private fun setObserver() {
        viewModel.rvNurseAvailable.observe(viewLifecycleOwner) {
            binding.rvNurses.adapter = AdapterNurseAvailable(it) { nurse: NurseRegister ->
                nurseSelect = nurse
            }
        }
        viewModel.modelAppointment.observe(viewLifecycleOwner) {
            if (nurseSelect != null) {
                if (binding.btnAddAppointment.text.equals("Seleccionar Direccion")) {
                    findNavController().navigate(AppointmentUserSecondScreenFragmentDirections.actionAppointmentUserSecondScreenFragmentToAddressFragment(it))
                } else {
                    it.uidNurse = nurseSelect!!.uid
                    viewModel.setAppointment(
                        arrayOf(
                            valueSpinner,
                            binding.tvTimeSelected.text.toString(),
                            binding.tvDateSelected.text.toString(),
                            nurseSelect!!.uid
                            ), it
                    )
                }
            }
        }

        viewModel.isProgress.observe(viewLifecycleOwner) {
            if (it.first) {

                if(it.second==2 || it.second == 3){
                    binding.loading.visibility = View.VISIBLE
                    binding.rvNurses.visibility = View.GONE
                    binding.messageLoading.setText("CARGANDO DISPONIBILIDAD . . .")
                    if(it.second == 3){
                        binding.messageLoading.setText("No se encuentra disponibilidad para la hora seleccionada. Intenta de nuevo")
                    }
                }else{
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }
                    progressDialog.show(childFragmentManager, "Progress Fragment")
                }


            } else {
                if(it.second==2){
                    binding.loading.visibility = View.GONE
                    binding.rvNurses.visibility = View.VISIBLE
                }else{
                    if (progressDialog.isVisible) {
                        progressDialog.dismiss()
                    }
                }

            }
        }

        viewModel.informationFragment.observe(viewLifecycleOwner) {

            if (it == null) return@observe

            informationDialog = InformationFragment()
            informationDialog!!.getInstance("ATENCION", if(it == "0") getString(R.string.data_update) else it )

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationDialog!!.isVisible) {
                        informationDialog!!.dismiss()
                    }
                    if(it == "0"){

                    }
                }
            }, 3000)

            if (!informationDialog!!.isAdded) {
                informationDialog!!.show(requireActivity().supportFragmentManager, "gg")
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.tvDateSelected.text = Utils().getCurrentDate(calendar.timeInMillis)
                showTimePickerDialog()

            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = TimePickerDialog(
            requireContext(),
            object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                    binding.tvTimeSelected.setText("$p1 : $p2")
                    viewModel.getNurse(binding.tvDateSelected.text.toString(), p1)
                }
            }, currentHour, currentMinute, true
        )

        datePickerDialog.show()
    }

    private fun setSpinner() {
        val adapterTypeAppointment = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.typeAppoiment_array)
        )

        adapterTypeAppointment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spTypeAppoiment.adapter = adapterTypeAppointment
        binding.spTypeAppoiment.onItemSelectedListener = this
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        valueSpinner = adapterView?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        println("")
    }
}