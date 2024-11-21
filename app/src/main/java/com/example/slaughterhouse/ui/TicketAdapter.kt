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
import com.example.slaughterhouse.util.PreferenceManager

class TicketAdapter(
    var ticketList: MutableList<PendingTicketsResponse>,
    private val onItemSelected: (PendingTicketsResponse) -> Unit, // Callback to return selected item
    private val proceedTicket: (pendingTicketsResponse: PendingTicketsResponse) -> Unit,
    private val holdTicket: (pendingTicketsResponse: PendingTicketsResponse) -> Unit,
    private val rejectTicket: (pendingTicketsResponse: PendingTicketsResponse) -> Unit,
    private val showProceed: String, // "1" to show, "0" to hide
    private val showHold: String,
    private val showReject: String
) : RecyclerView.Adapter<TicketAdapter.ItemViewHolder>() {




    private var selectedPosition = RecyclerView.NO_POSITION // Track selected position
    private var lockedPosition: Int? = null // Track locked position (highlighted item)

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketNumber: TextView = itemView.findViewById(R.id.ticket_number_cell)
        val ticketDescription: TextView = itemView.findViewById(R.id.ticket_description_cell)
        val ticketStatus: TextView = itemView.findViewById(R.id.ticket_status_cell)
        val proceedBtn: CardView = itemView.findViewById(R.id.proceed)
        val holdBtn: CardView = itemView.findViewById(R.id.hold)
        val rejectBtn: CardView = itemView.findViewById(R.id.reject)
        val rejectText: TextView = itemView.findViewById(R.id.tv_reject)
        val layout: LinearLayout = itemView.findViewById(R.id.layout_status)

        init {
            itemView.setOnClickListener {
                if (lockedPosition == null) {
                    // If no item is locked, allow selection
                    if (selectedPosition == adapterPosition) {
                        // Unhighlight selected item if clicked again
                        selectedPosition = RecyclerView.NO_POSITION
                        lockedPosition = null
                    } else {
                        // Lock the selected item
                        selectedPosition = adapterPosition
                        lockedPosition = selectedPosition // Lock the row
                        onItemSelected(ticketList[selectedPosition]) // Return selected item
                    }
                    notifyDataSetChanged() // Refresh the list
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
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

        holder.proceedBtn.visibility = if (showProceed == "1") View.VISIBLE else View.GONE
        holder.holdBtn.visibility = if (showHold == "1") View.VISIBLE else View.GONE
        holder.rejectBtn.visibility = if (showReject == "1") View.VISIBLE else View.GONE

        // Check the item status and lock the position if needed
        if (item.status == 2 || lockedPosition == currentPosition || selectedPosition == currentPosition) {
            holder.itemView.setBackgroundColor(colorHighlight)
            lockedPosition = currentPosition // Lock the highlighted row
            PreferenceManager.saveSelectedTicket(context, item.ticketNO, true)
        } else {
            holder.itemView.setBackgroundColor(if (currentPosition % 2 == 0) colorGrey else colorLightPurple)
        }

        // Set ticket info
        holder.ticketNumber.text = item.ticketNO
        holder.ticketDescription.text = "${item.qServiceEn}\n${item.qServiceAr}"
        holder.ticketStatus.text = if (item.status == 2 || lockedPosition == currentPosition || selectedPosition == currentPosition) "Selected" else "Pending"

        // Button interactions when item is locked/selected
        if (lockedPosition == currentPosition || selectedPosition == currentPosition) {
            holder.proceedBtn.setOnClickListener { proceedTicket(item) }
            holder.holdBtn.setOnClickListener { holdTicket(item) }
            holder.rejectBtn.setOnClickListener { rejectTicket(item) }
        } else {
            // Disable buttons when not selected
            holder.proceedBtn.setOnClickListener(null)
            holder.holdBtn.setOnClickListener(null)
            holder.rejectBtn.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = ticketList.size

    // Method to remove the selected item and unlock other items
    fun removeItemById(ticketId: String) {
        val position = ticketList.indexOfFirst { it.id.toString() == ticketId }
        if (position != -1) {
            ticketList.removeAt(position)
            notifyItemRemoved(position)

            // If the removed ticket was the locked one, clear the lock
            if (lockedPosition == position) {
                lockedPosition = null
                selectedPosition = RecyclerView.NO_POSITION
            }
        }
    }


    // Optional: Programmatically lock a specific ticket (e.g., from API response)
    fun lockTicketById(ticketId: String) {
        val position = ticketList.indexOfFirst { it.id.toString() == ticketId }
        if (position != -1) {
            lockedPosition = position
            selectedPosition = position
            notifyDataSetChanged() // Refresh the list to reflect locked item
        }
    }
    fun selectTicketByNumber(ticketNumber: String) {
        val position = ticketList.indexOfFirst { it.ticketNO == ticketNumber }
        if (position != -1) {
            lockedPosition = position
            selectedPosition = position
            onItemSelected(ticketList[position]) // Trigger the selection callback with the selected item
            notifyDataSetChanged() // Refresh the list to reflect selection
        }
    }
}
