package com.uts.homelab.view.fragment.user

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentUserDataBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UserDataFragment : Fragment() , AdapterView.OnItemSelectedListener {

    private lateinit var binding:FragmentUserDataBinding
    private val viewModel:UserViewModel  by activityViewModels()

    private var valueSpinner = ""
    private var valueEPS  = ""


    private var gender: String? = null
    private var boolGender: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDataBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setModel(UserDataFragmentArgs.fromBundle(requireArguments()).userModel)
        spinner()
        setObserver()

        binding.etNacimiento.setOnClickListener{
            showDatePickerDialog()
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
            viewModel.userModel.value!!.age = binding.etAge.text.toString().toInt()
            viewModel.userModel.value!!.gender = gender!!
            viewModel.userModel.value!!.typeDocument = valueSpinner
            viewModel.userModel.value!!.valueDocument = binding.etNumberDocument.text.toString()
            viewModel.userModel.value!!.phone = binding.etTelefono.text.toString().toLong()
            viewModel.userModel.value!!.eps = valueEPS
            viewModel.userModel.value!!.nacimiento = selectedDateTimestamp
            viewModel.saveRoom()
           findNavController().navigate(UserDataFragmentDirections.actionUserDataFragmentToAddressFragment(
               AppointmentUserModel()
           ))
        }
    }
    private var selectedDateTimestamp = 0L
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
                 selectedDateTimestamp = calendar.timeInMillis
                binding.etAge.setText(calculateAge().toString())
                binding.etNacimiento.setText( selectedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }

    private fun calculateAge() : Int {

        val fechaNacimiento = Date(selectedDateTimestamp)
        val fechaActual = Date()

        val calendarNacimiento = Calendar.getInstance()
        calendarNacimiento.time = fechaNacimiento

        val calendarActual = Calendar.getInstance()
        calendarActual.time = fechaActual

        var años = calendarActual.get(Calendar.YEAR) - calendarNacimiento.get(Calendar.YEAR)

        // Verificar si la fecha actual aún no ha alcanzado la fecha de nacimiento de este año.
        if (calendarActual.get(Calendar.DAY_OF_YEAR) < calendarNacimiento.get(Calendar.DAY_OF_YEAR)) {
            años--
        }

        return años
    }

    private fun setObserver() {
        viewModel.userModel.observe(viewLifecycleOwner){
            binding.nameUser.text = it!!.name + " " + it!!.lastName.split(" ")[0]
        }
    }

    private val documents = listOf("CC", "DNI", "TI", "DE")
    private val eps = listOf("Salud total", "Sanitas", "SaludCoop", "Cafe salud")

    private fun spinner() {
        val adapterTypeDocument = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            documents
        )

        val adapterTypeEPS = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            eps
        )
        adapterTypeDocument.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterTypeEPS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTypeDocument.adapter = adapterTypeDocument
        binding.spTypeDocument.onItemSelectedListener = this

        binding.spTypeIPS.adapter = adapterTypeEPS
        binding.spTypeIPS.onItemSelectedListener = this
    }


    override fun onItemSelected(adapter: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        if(adapter.id == R.id.sp_typeIPS){
            valueEPS =  eps[p2]
        }else{
            valueSpinner = documents[p2]
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        println("")
    }

}