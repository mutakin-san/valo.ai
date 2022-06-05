package com.capstone.valoai.features.dashboard.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.valoai.R

class FakesListAdapter(private val fikestList: List<String>) : RecyclerView.Adapter<FakesListAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_fakes, parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.name.text = fikestList[position]
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(fikestList[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = fikestList.size

    interface OnItemClickCallback {
        fun onItemClicked(data: String)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.faskes_name)
    }
}