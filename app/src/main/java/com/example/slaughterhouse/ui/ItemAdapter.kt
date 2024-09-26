//package com.example.slaughterhouse.ui
//
//import android.annotation.SuppressLint
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.slaughterhouse.R
//
//class ItemAdapter(
//    private var items: List<Item>,
//    private var highlightedText: String = "" // Ensure it's empty initially
//) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
//
//    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val itemName: TextView = itemView.findViewById(R.id.item_name)
//        val container: View = itemView // The view container of the row
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
//        return ItemViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        val item = items[position]
//        holder.itemName.text = item.name
//
//        // Highlight only if highlightedText is not empty and matches the row's content
//        if (highlightedText.isNotEmpty() && item.name.contains(highlightedText, ignoreCase = true)) {
//            // Highlight the background if it matches
//            holder.container.setBackgroundColor(Color.YELLOW) // Highlight color
//        } else {
//            // Set no background color (transparent) by default
//            holder.container.setBackgroundColor(Color.TRANSPARENT)
//        }
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    // Update the highlighted text and refresh the list
//    @SuppressLint("NotifyDataSetChanged")
//    fun updateHighlightedText(newText: String) {
//        highlightedText = newText
//        notifyDataSetChanged()
//    }
//}
