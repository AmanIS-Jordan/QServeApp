package com.example.slaughterhouse.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slaughterhouse.R
import com.example.slaughterhouse.data.model.PendingTicketsResponse

class TicketAdapter(
    private var ticketList: MutableList<PendingTicketsResponse>,
    private val onItemSelected: (PendingTicketsResponse) -> Unit ,// Callback to return selected item
    private val proceedTicket : (pendingTicketsResponse : PendingTicketsResponse ) -> Unit ,
    private val holdTicket : (pendingTicketsResponse : PendingTicketsResponse ) -> Unit ,
    private val rejectTicket : (pendingTicketsResponse : PendingTicketsResponse ) -> Unit

    ) : RecyclerView.Adapter<TicketAdapter.ItemViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION // Keep track of selected position
    private var lockedPosition: Int? = null // Keep track of the locked (highlighted) position



    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number_cell)
        val ticketDescription: TextView = itemView.findViewById(R.id.ticket_description_cell)
        val ticketStatus: TextView = itemView.findViewById(R.id.ticket_status_cell)
        val proceedBtn : CardView = itemView.findViewById(R.id.proceed)
        val holdBtn : CardView = itemView.findViewById(R.id.hold)
        val rejectBtn : CardView = itemView.findViewById(R.id.reject)
        val rejecttext : TextView = itemView.findViewById(R.id.tv_reject)

        val layout : LinearLayout = itemView.findViewById(R.id.layout_status)

        init {
            itemView.setOnClickListener {
                if (lockedPosition == null) {
                    // If no row is locked (highlighted), allow selection
                    if (selectedPosition == adapterPosition) {
                        // Unhighlight the selected row if clicked again
                        selectedPosition = RecyclerView.NO_POSITION
                        lockedPosition = null
                    } else {
                        // Highlight the clicked row
                        selectedPosition = adapterPosition
                        onItemSelected(ticketList[selectedPosition]) // Call the callback when an item is selected

                    }
                    notifyDataSetChanged() // Refresh the list
                } else if (lockedPosition == adapterPosition) {
                    // Unhighlight if the same locked row is clicked again
                    lockedPosition = null
                    selectedPosition = RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate the item layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_cell, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val context = holder.itemView.context
        val colorLightPurple = ContextCompat.getColor(context, R.color.light_purple)
        val colorGrey = ContextCompat.getColor(context, R.color.light_grey)
        val colorHighlight = ContextCompat.getColor(context, R.color.highlight)

        val currentPosition = holder.adapterPosition
        if (currentPosition == RecyclerView.NO_POSITION) return

        val item = ticketList[currentPosition]

        // Check the item status and highlight if status == 2 or it's the selected row
        if (item.status == 2 || selectedPosition == currentPosition) {
            holder.itemView.setBackgroundColor(colorHighlight)
            lockedPosition = currentPosition // Lock the highlighted row
        } else {
            holder.itemView.setBackgroundColor(if (currentPosition % 2 == 0) colorGrey else colorLightPurple)
        }

        // Set ticket info
        holder.ticketNumber.text = item.ticketNO
        holder.ticketDescription.text = "${item.qServiceEn}\n${item.qServiceAr}"
        holder.ticketStatus.text = if (item.status == 2 || selectedPosition == currentPosition) "Selected" else "Pending"

        // Button interactions only when status == 2 or the row is selected
        if (item.status == 2 || selectedPosition == currentPosition) {
            holder.proceedBtn.setOnClickListener { proceedTicket(item) }
            holder.holdBtn.setOnClickListener { holdTicket(item) }
            holder.rejectBtn.setOnClickListener { rejectTicket(item) }

            holder.rejecttext.isClickable = true
            holder.rejectBtn.isClickable = true
        } else {
            // Disable buttons if row is not selected
            holder.rejecttext.isClickable = false
            holder.rejectBtn.isClickable = false
        }
    }

    override fun getItemCount(): Int {
        // Return the total number of items
        return ticketList.size
    }

    fun removeItemById(ticketId: String) {
        val position = ticketList.indexOfFirst { it.id.toString() == ticketId }
        if (position != -1) {
            ticketList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
