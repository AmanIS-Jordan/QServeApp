package com.example.slaughterhouse.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slaughterhouse.R


class TicketAdapter(private var itemList: List<Names>) : RecyclerView.Adapter<TicketAdapter.ItemViewHolder>() {



    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number_cell)
        val ticketDescription: TextView = itemView.findViewById(R.id.ticket_description_cell)
        val ticketStatus: TextView = itemView.findViewById(R.id.ticket_status_cell)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate the item layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_cell, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val context = holder.itemView.context

        // Get colors from resources
        val colorLightPurple = ContextCompat.getColor(context, R.color.light_purple)
        val colorGrey = ContextCompat.getColor(context, R.color.light_grey)

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(colorGrey) // Color for even rows
        } else {
            holder.itemView.setBackgroundColor(colorLightPurple) // Color for odd rows
        }
        // Bind data to the ViewHolder
        val item = itemList[position]
        holder.ticketNumber.text = item.name1
        holder.ticketDescription.text =item.name2
        holder.ticketStatus.text=item.name3
    }

    override fun getItemCount(): Int {
        // Return the total number of items
        return itemList.size
    }
}
