package com.uts.homelab.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uts.homelab.R
import com.uts.homelab.databinding.AdapterNurseAvailableBinding
import com.uts.homelab.network.dataclass.NurseRegister

class AdapterNurseAvailable(private val listData: List<NurseRegister>,private val onClick:(s:NurseRegister)->Unit) :
    RecyclerView.Adapter<AdapterNurseAvailable.ViewHolder>() {

    private var selectedItemPosition = RecyclerView.NO_POSITION
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding by lazy { AdapterNurseAvailableBinding.bind(view) }

        fun render(modelNurse: NurseRegister) {
            binding.nameNurse.text = modelNurse.name
            binding.lastname.text = modelNurse.lastName!!.split(" ")[0]

            if (modelNurse.gender == "F") binding.imageView2.background = ContextCompat.getDrawable(
                    binding.root.context,
            R.drawable.nurse_women
            ) else binding.imageView2.background =
            ContextCompat.getDrawable(binding.root.context, R.drawable.nurse_men)

            if (position == selectedItemPosition) {
                binding.cardItem.strokeWidth =
                    binding.root.context.resources.getDimensionPixelSize(R.dimen.stroke_width)
                binding.cardItem.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.blue_alianza)

            } else {
                binding.cardItem.strokeWidth =
                    binding.root.context.resources.getDimensionPixelSize(R.dimen.stroke_card_0)
                binding.cardItem.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.white)
            }

            binding.cardItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedItemPosition = position
                    notifyDataSetChanged()
                }
                onClick(modelNurse)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = AdapterNurseAvailableBinding.inflate(inflate, parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listData[position])

    }
}