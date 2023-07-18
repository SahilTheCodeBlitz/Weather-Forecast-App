package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter (private val list:List<Weather>):RecyclerView.Adapter<MyAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.items, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPos = list[position]


        holder.tvDate.text =currentPos.date

        holder.tvTemp.text = currentPos.temp

        holder.tvDes.text = currentPos.des



    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTemp :TextView = itemView.findViewById(R.id.tvTemperature)
        val tvDes: TextView = itemView.findViewById(R.id.tvDes)

    }
}