package com.capstone.valoai.features.detail_faskes.domain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.valoai.databinding.VaccineTypeItemLayoutBinding

class VaccineTypeAdapter(private val availableVaccineType: List<String>) :
    RecyclerView.Adapter<VaccineTypeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VaccineTypeAdapter.ViewHolder {
        return ViewHolder(
            VaccineTypeItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: VaccineTypeAdapter.ViewHolder, position: Int) {
        holder.bind(availableVaccineType[position])
    }

    override fun getItemCount() = availableVaccineType.size


    inner class ViewHolder(
        private val binding: VaccineTypeItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(type: String){
            binding.vaccineType.text = type
        }
    }
}