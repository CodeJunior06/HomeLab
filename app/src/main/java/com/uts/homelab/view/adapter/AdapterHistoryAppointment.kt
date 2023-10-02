package com.uts.homelab.view.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.R
import com.uts.homelab.databinding.AdapterHistoryAppointmentBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.State

class AdapterHistoryAppointment(
    private val listData: List<AppointmentUserModel>,
    private val typeUser: Rol
) :
    RecyclerView.Adapter<AdapterHistoryAppointment.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterHistoryAppointmentBinding.bind(view) }


        @RequiresApi(Build.VERSION_CODES.O)
        fun render(appointmentModel: AppointmentUserModel) {
            getView(typeUser,appointmentModel)

        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getView(typeUser: Rol, appointmentModel: AppointmentUserModel) {
            var hour = appointmentModel.hour.split(" : ")[0]
            var minute = appointmentModel.hour.split(" : ")[0]

            binding.name.text =
                if (typeUser == Rol.USER)
                    "${appointmentModel.modelNurse.name} ${
                        appointmentModel.modelNurse.lastName.split(
                            " "
                        )[0]
                    }" else "${appointmentModel.modelUser.name} ${
                    appointmentModel.modelUser.lastName.split(
                        " "
                    )[0]
                }"

            binding.state.text = appointmentModel.state
            binding.dateExam.text = appointmentModel.date + "  " +hour + ":" + minute
            binding.typeExam.text = appointmentModel.typeOfExam

            when(appointmentModel.state){
                State.ACTIVO.name->{
                    binding.state.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
                State.CURSO.name->{
                    binding.llBtn.visibility = View.GONE
                    binding.state.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                }
                State.CANCELADO.name->{
                    binding.llBtn.visibility = View.VISIBLE
                    binding.btnCancelOrProblem.setText("Reportar poblema")
                    binding.state.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                }
                State.LABORATORIO.name->{
                    binding.llBtn.visibility = View.VISIBLE
                    binding.btnCancelOrProblem.setText("Reportar poblema")
                    binding.state.setTextColor(ContextCompat.getColor(binding.root.context, R.color.yellow))
                }
                State.FINALIZADO.name->{
                    binding.llBtn.visibility = View.GONE
                    binding.state.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray))
                }
            }



            if(typeUser == Rol.USER){
                /*binding.imgTypeExam.setImageDrawable(
                    if (appointmentModel.modelNurse.gender.equals("F", true)) ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.nurse_women
                    ) else ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.nurse_men
                    )
                )*/
            }else{
               /* binding.imgTypeExam.setImageDrawable(
                    if (appointmentModel.modelNurse.gender.equals("F", true)) ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.women_user
                    ) else ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.men_user
                    )
                )*/
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = AdapterHistoryAppointmentBinding.inflate(inflate, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listData.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listData[position])

    }
}