package com.uts.homelab.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.databinding.AdapterNurseAppointmentBinding

class AdapterNurseAppointment(private val listData: ArrayList<*>) :
    RecyclerView.Adapter<AdapterNurseAppointment.ViewHolder>() {


    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterNurseAppointmentBinding.bind(view) }

        fun render(any: Any) {

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