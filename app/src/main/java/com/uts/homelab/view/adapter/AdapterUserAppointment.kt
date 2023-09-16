package com.uts.homelab.view.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.R
import com.uts.homelab.databinding.AdapterUserAppointmentBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.TypeView
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AdapterUserAppointment(private val listData: List<AppointmentUserModel>,private val typeView: TypeView) :
    RecyclerView.Adapter<AdapterUserAppointment.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterUserAppointmentBinding.bind(view) }

        private var appointment:AppointmentUserModel? = null
        private val appointmentModel:AppointmentUserModel by lazy { appointment!! }

        @RequiresApi(Build.VERSION_CODES.O)
        fun render(appointmentModel: AppointmentUserModel) {
            this.appointment = appointmentModel
            when(typeView){
                TypeView.HISTORY-> getFilterViewHistory()
                TypeView.RESULT -> getFiterViewResult()
                TypeView.MAIN->getView()
            }

        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getView() {
            var hour = appointmentModel.hour.split(" : ")[0]
            var minute = appointmentModel.hour.split(" : ")[0]

            binding.nameNurse.text =
                "${appointmentModel.modelNurse.name} ${appointmentModel.modelNurse.lastName!!.split(" ")[0]}"
            binding.state.text = appointmentModel.state
            binding.hourExam.text =  hour+ ":" + minute
            binding.typeExam.text = appointmentModel.typeOfExam

            binding.imgTypeExam.setImageDrawable(
                if (appointmentModel.modelNurse.gender.equals("F", true)) ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.nurse_women
                ) else ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.nurse_men
                )
            )
            binding.initAppointment.isEnabled = false

            val horaDada: LocalTime = LocalTime.of(hour.toInt(),minute.toInt())
            val horaAntes: LocalTime = horaDada.minusHours(1)
            val horaDespues: LocalTime = horaDada.plusHours(1)

            val horaActual = LocalTime.now()
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val horaFormateada = horaActual.format(formatter)
            println("Hora actual: $horaFormateada")
            hour = horaFormateada.split(":")[0]
            minute = horaFormateada.split(":")[0]

            if((horaAntes.hour > hour.toInt() && horaAntes.minute> minute.toInt()) ||(horaDespues.hour < hour.toInt() && horaDespues.minute< minute.toInt())){
                binding.initAppointment.isEnabled = true
            }

        }

        private fun getFiterViewResult() {

        }

        private fun getFilterViewHistory() {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = AdapterUserAppointmentBinding.inflate(inflate, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listData.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listData[position])

    }
}