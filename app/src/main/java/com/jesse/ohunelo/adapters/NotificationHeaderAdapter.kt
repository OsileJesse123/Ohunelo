package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.NotificationHeaderItemBinding

class NotificationHeaderAdapter(private val onClick: () -> Unit): RecyclerView.Adapter<NotificationHeaderAdapter.NotificationHeaderViewHolder>() {

    private var cachedHolder: NotificationHeaderViewHolder? = null

    var shouldShowRationale = false
        set(value) {
            field = value
            cachedHolder?.let { holder ->
                onBindViewHolder(holder, 0)
            }
        }

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHeaderViewHolder {
        return NotificationHeaderViewHolder(parent).also {
            cachedHolder = it
            it.binding.grantPermission.setOnClickListener { onClick() }
        }
    }

    override fun onBindViewHolder(holder: NotificationHeaderViewHolder, position: Int) {
        holder.binding.permsissionRationale.visibility =
            if (shouldShowRationale) View.VISIBLE else View.GONE
    }

    class NotificationHeaderViewHolder(
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.notification_header_item, parent, false)
    ) {
        val binding = NotificationHeaderItemBinding.bind(itemView)
    }
}