package com.capstone.valoai.features.dashboard.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.valoai.R
import com.capstone.valoai.features.dashboard.data.models.RiwayatVaksin

class RiwayatListAdapter(private val fikestList: List<RiwayatVaksin>): RecyclerView.Adapter<RiwayatListAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_riwayat, parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.name.text = fikestList[position].vaksin
        holder.date.text = fikestList[position].date
        holder.nomorVaksin.text = fikestList[position].ke
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(fikestList[holder.adapterPosition]) }

        if(fikestList[position].vaksin.isEmpty() || fikestList[position].vaksin == "n/a"){
            holder.card.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = fikestList.size

    interface OnItemClickCallback {
        fun onItemClicked(data: RiwayatVaksin)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.faskes_name)
        val date: TextView = itemView.findViewById(R.id.tanggal_riwayat_vaksin)
        val card: CardView = itemView.findViewById(R.id.riwayat_card)
        val nomorVaksin: TextView = itemView.findViewById(R.id.vaksin_ke)
    }
}