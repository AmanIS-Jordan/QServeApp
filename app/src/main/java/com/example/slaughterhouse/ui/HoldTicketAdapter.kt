package com.example.slaughterhouse.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slaughterhouse.R
import com.example.slaughterhouse.data.model.HoldTicketsList

class HoldTicketAdapter (
    private var holdTicketList: MutableList<HoldTicketsList>,
    private val onItemSelected: (HoldTicketsList) -> Unit , // Callback to return selected item
    private val recallTicket : (holdTicketsList : HoldTicketsList) -> Unit
    ) : RecyclerView.Adapter<HoldTicketAdapter.ItemViewHolder>() {

        private var selectedPosition = RecyclerView.NO_POSITION // Keep track of selected position

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number_cell)
            val ticketDescription: TextView = itemView.findViewById(R.id.ticket_description_cell)
            val ticketStatus: TextView = itemView.findViewById(R.id.ticket_status_cell)
            val recallBtn : CardView = itemView.findViewById(R.id.recall)

            init {
                itemView.setOnClickListener {
                    // Update the selected position and notify the adapter to refresh
                    notifyItemChanged(selectedPosition) // Refresh previously selected item
                    selectedPosition = adapterPosition
                    notifyItemChanged(selectedPosition) // Refresh currently selected item

                    // Trigger the callback to return the selected item
                    val selectedItem = holdTicketList[selectedPosition]
                    onItemSelected(selectedItem)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            // Inflate the item layout and create the ViewHolder
            val view = LayoutInflater.from(parent.context).inflate(R.layout.hold_ticket_cell, parent, false)
            return ItemViewHolder(view)
        }

        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val context = holder.itemView.context

            // Get colors from resources
            val colorLightPurple = ContextCompat.getColor(context, R.color.light_purple)
            val colorGrey = ContextCompat.getColor(context, R.color.light_grey)

            // Highlight the selected row
            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight))
            } else {
                // Set default background color based on position
                holder.itemView.setBackgroundColor(if (position % 2 == 0) colorGrey else colorLightPurple)
            }

            // Bind data to the ViewHolder
            val item = holdTicketList[position]
            holder.ticketNumber.text = item.ticketNo
            holder.ticketDescription.text = "${item.qServiceEn}\n${item.qServiceAr}"
            holder.ticketStatus.text = ""

            // Optional: fill some info when selected
            if (position == selectedPosition) {
                holder.ticketStatus.text = "Selected" // Example of showing status for selected item
            }

            holder.recallBtn.setOnClickListener{
                recallTicket(item)

            }
        }

        override fun getItemCount(): Int {
            // Return the total number of items
            return holdTicketList.size
        }


    fun removeItemById(ticketId: String) {
        val position = holdTicketList.indexOfFirst { it.id == ticketId }
        if (position != -1) {
            holdTicketList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}
