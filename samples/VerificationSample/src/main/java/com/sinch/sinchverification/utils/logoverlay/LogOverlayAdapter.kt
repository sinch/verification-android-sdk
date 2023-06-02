package com.sinch.sinchverification.utils.logoverlay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sinch.sinchverification.databinding.ItemOverlayLogBinding

class LogOverlayAdapter() : RecyclerView.Adapter<LogOverlayAdapter.ViewHolder>() {

    var items: List<LogOverlayItem> = emptyList()
        set(newVal) {
            field = newVal
            notifyDataSetChanged()
        }

    fun addItem(item: LogOverlayItem) {
        items = items.plus(item)
    }

    class ViewHolder(private val binding: ItemOverlayLogBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LogOverlayItem) {
            with(binding) {
                logTagTextView.text = item.tag
                logTagTextView.setTextColor(item.level.getColor(itemView.context))
                logMessageTextView.text = item.message
                logMessageTextView.setTextColor(item.level.getColor(itemView.context))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemOverlayLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}