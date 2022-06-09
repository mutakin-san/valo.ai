package com.capstone.valoai.features.dashboard.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.valoai.R
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel


class FakesListAdapter(private val fikestList: List<FaskesModel>, val ctx: Context) :
    RecyclerView.Adapter<FakesListAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var params = MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_fakes, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.name.text = fikestList[position].name
        holder.labelsLayout.removeAllViews()


        fikestList[position].availableVaccineType.take(3).toList().forEach {
            val item = TextView(ctx)
            item.text = it
            item.setTextColor(0xFFFFFFFF.toInt())
            item.setPadding(6, 2, 6, 2)
            val card = CardView(ctx)
            card.addView(item)
            params.setMargins(0,0,20,0)
            card.radius = 7F
            card.setCardBackgroundColor(0xff28B7DD.toInt())
            card.layoutParams = params
            holder.labelsLayout.addView(card)
        }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(fikestList[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = fikestList.size


    interface OnItemClickCallback {
        fun onItemClicked(data: FaskesModel)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.faskes_name)
        val labelsLayout: LinearLayout = itemView.findViewById(R.id.lebels_view)
        val label: CardView = itemView.findViewById(R.id.vaksin_card)
    }

    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }
}

