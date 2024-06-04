package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.NotificationHeaderItemBinding

class NotificationHeaderAdapter(
    private var onClick: () -> Unit,
    private val permissionRationaleMessage: String
): RecyclerView.Adapter<NotificationHeaderAdapter.NotificationHeaderViewHolder>() {

    private var cachedHolder: NotificationHeaderViewHolder? = null

    var shouldShowRationale = false
        set(value) {
            field = value
            cachedHolder?.let { holder ->
                onBindViewHolder(holder, 0)
            }
        }
    var shouldGuideUserToAppSettings = false
    fun updateOnClick(onClick: () -> Unit){
        this.onClick = onClick
    }
    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHeaderViewHolder {
        return NotificationHeaderViewHolder(parent).also {
            cachedHolder = it
            it.binding.grantPermission.setOnClickListener { onClick() }
        }
    }

    override fun onBindViewHolder(holder: NotificationHeaderViewHolder, position: Int) {
        holder.binding.permsissionRationale.apply {
            text = permissionRationaleMessage
            visibility = if (shouldShowRationale) View.VISIBLE else View.GONE
        }
        holder.binding.permissionRequired.visibility = if(shouldGuideUserToAppSettings && !shouldShowRationale) View.VISIBLE else View.GONE
        if (shouldGuideUserToAppSettings && !shouldShowRationale){
            holder.binding.grantPermission.apply {
                text = this.resources.getString(R.string.grant_permission)
            }
        }
    }

    class NotificationHeaderViewHolder(
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.notification_header_item, parent, false)
    ) {
        val binding = NotificationHeaderItemBinding.bind(itemView)
    }
}