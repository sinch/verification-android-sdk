package com.sinch.sinchverification.utils.logoverlay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sinch.sinchverification.R
import kotlinx.android.synthetic.main.item_overlay_log.view.*

class LogOverlayAdapter() : RecyclerView.Adapter<LogOverlayAdapter.ViewHolder>() {

    var items: List<LogOverlayItem> = emptyList()
        set(newVal) {
            field = newVal
            notifyDataSetChanged()
        }

    fun addItem(item: LogOverlayItem) {
        items = items.plus(item)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(tag: String, message: String) {
            itemView.logTagTextView.text = tag
            itemView.logMessageTextView.text = message
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_overlay_log, parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position].tag, items[position].message)
    }

    override fun getItemCount(): Int = items.size
}