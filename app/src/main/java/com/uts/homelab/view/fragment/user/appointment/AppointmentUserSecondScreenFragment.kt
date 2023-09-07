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
import com.uts.homelab.viewmodel.userViewmodel.AppointmentUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AppointmentUserSecondScreenFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentAppointmentUserSecondScreenBinding? = null
    private val viewModel: AppointmentUserViewModel by activityViewModels()
    private val binding get() = _binding!!

    private var valueSpinner = ""

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
            if(binding.btnAddAppointment.text.equals("Seleccionar Direccion")){
                findNavController().navigate(AppointmentUserSecondScreenFragmentDirections.actionAppointmentUserSecondScreenFragmentToAddressFragment())
            }else{
                viewModel.setAppointment(
                    arrayOf(
                        valueSpinner,
                        binding.tvTimeSelected.text.toString(),
                        binding.tvDateSelected.text.toString(),
                    )
                )
            }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinner()

        binding.tvDateSelected.setOnClickListener {
            showDatePickerDialog()
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
                val selectedDate = "$dayOfMonth / ${month + 1} / $year"
                calendar.set(year, month, dayOfMonth)
               // selectedDateTimestamp = calendar.timeInMillis
                binding.tvDateSelected.text = selectedDate
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
            object: TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                    binding.tvTimeSelected.setText("$p1 : $p2")
                    //viewModel.getNurse(binding.tvDateSelected.text.toString(),binding.tvTimeSelected.text.toString())
                }
            },currentHour,currentMinute,true
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
       valueSpinner =  adapterView?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
       println("")
    }
}