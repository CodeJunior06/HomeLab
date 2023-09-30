package com.uts.homelab.view.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.databinding.AdapterUserAppointmentBinding
import com.uts.homelab.databinding.AdapterUserResultBinding
import com.uts.homelab.network.dataclass.AppointmentUserModel
import com.uts.homelab.utils.Rol

class AdapterUserResult(
    private val listData: List<AppointmentUserModel>,
    private val typeUser: Rol,
    private val  call: (appointment:AppointmentUserModel) ->Unit
) :
    RecyclerView.Adapter<AdapterUserResult.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterUserResultBinding.bind(view) }



        @RequiresApi(Build.VERSION_CODES.O)
        fun render(appointmentModel: AppointmentUserModel) {

            getView(typeUser,appointmentModel)

        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getView(typeUser: Rol, appointmentModel: AppointmentUserModel) {
            val hour = appointmentModel.hour.split(" : ")[0]
            val minute = appointmentModel.hour.split(" : ")[0]

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
            binding.dateExam.text = appointmentModel.date + "   " + hour + ":" + minute
            binding.typeExam.text = appointmentModel.typeOfExam

            binding.btnDowloadPdf.setOnClickListener {
                call(appointmentModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = AdapterUserResultBinding.inflate(inflate, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listData.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listData[position])

    }
}