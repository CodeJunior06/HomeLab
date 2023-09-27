package com.uts.homelab.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.R
import com.uts.homelab.databinding.AdapterNurseAppointmentBinding
import com.uts.homelab.network.dataclass.NurseWorkingAdapter

class AdapterNurseAppointment(private val listData: List<NurseWorkingAdapter>) :
    RecyclerView.Adapter<AdapterNurseAppointment.ViewHolder>() {

    private var selectedItemPosition = -1

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterNurseAppointmentBinding.bind(view) }

        fun render(nurseWorking: NurseWorkingAdapter) {
            binding.initWord.text = nurseWorking.name.substring(0, 1)
            binding.name.text = nurseWorking.name.split(" ")[0]  +" "+ nurseWorking.lastName
            binding.phone.text = nurseWorking.phone
            binding.idMoto.text = nurseWorking.idMotorcycle

            if (nurseWorking.active) {
                binding.state.text = "En servicio"
                binding.llState.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.green_light
                    )
                )
                binding.btnGoActivity.visibility = View.VISIBLE
            } else {
                binding.state.text = "Fuera de servicio"
                binding.llState.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red_light
                    )
                )
            }

            binding.cardItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedItemPosition = position
                    notifyDataSetChanged()
                }
            }

            if (position == selectedItemPosition) {
                binding.cardItem.strokeWidth =
                    binding.root.context.resources.getDimensionPixelSize(R.dimen.stroke_width)
                binding.cardItem.strokeColor =
                    if(nurseWorking.active){
                        ContextCompat.getColor(binding.root.context, R.color.green_light)
                    }else{
                        ContextCompat.getColor(binding.root.context, R.color.red_light)
                    }
                binding.btnGoActivity.isEnabled = true


            } else {
                binding.cardItem.strokeWidth =
                    binding.root.context.resources.getDimensionPixelSize(R.dimen.stroke_width_0)
                binding.cardItem.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.white)
                binding.btnGoActivity.isEnabled = false

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = AdapterNurseAppointmentBinding.inflate(inflate, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listData[position])

    }
}