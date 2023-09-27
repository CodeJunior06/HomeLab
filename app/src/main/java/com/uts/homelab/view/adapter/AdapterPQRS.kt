package com.uts.homelab.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.R
import com.uts.homelab.databinding.AdapterPqrsBinding
import com.uts.homelab.network.dataclass.CommentType
import com.uts.homelab.utils.Opinion
import com.uts.homelab.utils.Rol
import com.uts.homelab.utils.Utils

class AdapterPQRS(private val listData: List<CommentType>) :
    RecyclerView.Adapter<AdapterPQRS.ViewHolder>() {


    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterPqrsBinding.bind(view) }

        fun render(commentType: CommentType) {
            binding.title.text = commentType.title
            binding.editTextTextMultiLine.setText(commentType.message)
            binding.tsPerson.text = Utils().getCurrentDate(commentType.ts)
            binding.namePerson.text = commentType.name
            when(commentType.type){
                Opinion.PROBLEM.name -> {
                    binding.typeIncident.text = commentType.type
                    binding.typeIncident.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.red_light))
                }
                Opinion.IMPROVEMENT.name -> {
                    binding.typeIncident.text = commentType.type
                    binding.typeIncident.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.green_light))
                }
                else ->{
                    binding.typeIncident.text = commentType.type
                    binding.typeIncident.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.background_editText))
                }
            }

            binding.typeUser.text = if(commentType.rol == Rol.USER.name){
                binding.typeUser.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.blue_hospital_light))
                commentType.rol
            }else{
                binding.typeUser.setBackgroundColor(ContextCompat.getColor(binding.root.context,android.R.color.holo_purple))
                commentType.rol
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = AdapterPqrsBinding.inflate(inflate, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listData[position])
    }
}