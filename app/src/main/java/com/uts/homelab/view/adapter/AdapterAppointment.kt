package com.uts.homelab.view.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.R
import com.uts.homelab.databinding.AdapterUserAppointmentBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AdapterAppointment(
    private val listData: List<AppointmentUserModel>,
    private val typeUser: Rol,
    private val onResult: OnResult
) :
    RecyclerView.Adapter<AdapterAppointment.ViewHolder>() {



    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterUserAppointmentBinding.bind(view) }

        @RequiresApi(Build.VERSION_CODES.O)
        fun render(appointmentModel: AppointmentUserModel) {
            getView(typeUser,appointmentModel)

        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getView(typeUser: Rol, appointmentModel: AppointmentUserModel) {
            var hour = appointmentModel.hour.split(" : ")[0]
            var minute = appointmentModel.hour.split(" : ")[1]

            binding.name.text =
                if (typeUser == Rol.USER)
                    "${appointmentModel.modelNurse.name} ${
                        appointmentModel.modelNurse.lastName}" else "${appointmentModel.modelUser.name} ${
                    appointmentModel.modelUser.lastName}"

            binding.state.text = appointmentModel.state
            binding.hourExam.text = hour + ":" + minute
            binding.typeExam.text = appointmentModel.typeOfExam
            if (typeUser == Rol.USER) {
                binding.imgTypeExam.setImageDrawable(
                    if (appointmentModel.modelNurse.gender.equals(
                            "F",
                            true
                        )
                    ) ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.nurse_women
                    ) else ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.nurse_men
                    )
                )
            } else {
                binding.btnCancelOrProblem.visibility = View.GONE

                binding.imgTypeExam.setImageDrawable(
                    if (appointmentModel.modelUser.gender.equals(
                            "F",
                            true
                        )
                    ) ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.women_user
                    ) else ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.men_user
                    )
                )
            }
            var res = false
            var res2 = false
            if (typeUser == Rol.USER) {
                val horaDada: LocalTime = LocalTime.of(hour.toInt(), minute.toInt())
                val horaAntes: LocalTime = horaDada.minusHours(1)
                val horaDespues: LocalTime = horaDada.plusHours(1)

                val horaActual = LocalTime.now()
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                val horaFormateada = horaActual.format(formatter)
                println("Hora actual: $horaFormateada")
                val  currentHour = horaFormateada.split(":")[0].toInt()
                val currentMinute = horaFormateada.split(":")[1].toInt()

                res = ( currentHour >= horaAntes.hour )
                res2 = ( horaDespues.hour <= currentHour)

                if (res || res2) {
                    if(res){
                        binding.btnCancelOrProblem.visibility = View.GONE
                    }
                    if(res2){
                        binding.btnInitAppointment.visibility = View.GONE
                    }
                }

            }else{
                val horaDada: LocalTime = LocalTime.of(hour.toInt(), minute.toInt())
                val horaAntes: LocalTime = horaDada.minusHours(2)

                val horaActual = LocalTime.now()
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                val horaFormateada = horaActual.format(formatter)
                println("Hora actual: $horaFormateada")
                val currentHour = horaFormateada.split(":")[0].toInt()
                val currentMinute = horaFormateada.split(":")[1].toInt()

                res = ( currentHour >= horaAntes.hour )
            }

            when(appointmentModel.state){
                State.ACTIVO.name->{
                    binding.state.text = "ACTIVO"
                    binding.state.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blue_hospital))
                }
                State.CURSO.name->{
                    binding.state.text = "EN PROGRESO"
                    binding.btnCancelOrProblem.visibility = View.GONE
                    binding.state.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
                }
                State.CANCELADO.name->{
                    binding.state.text = "CANCELADA"
                    binding.btnInitAppointment.visibility = View.GONE
                    binding.btnCancelOrProblem.visibility = View.GONE
                    binding.state.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.red))
                }
            }


            binding.btnInitAppointment.setOnClickListener {
                if (typeUser == Rol.USER) {
                    if(res2 || res){
                        onResult.onSuccess(appointmentModel)
                    }else{
                        Toast.makeText(binding.root.context, "estas queriendo acceder una hora antes o despues y no es posible", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if(!res){
                        Toast.makeText(binding.root.context, "Puedes iniciar la actividad en dos horas de la hora solicitada", Toast.LENGTH_SHORT).show()
                    }else{
                        onResult.onSuccess(appointmentModel)
                    }

                }

            }

            binding.btnCancelOrProblem.setOnClickListener {
                Toast.makeText(binding.root.context, "Accion de eliminar el documento de la coleccion", Toast.LENGTH_SHORT).show()
            }

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