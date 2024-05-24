package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.model.NotificationUIItem
import com.jesse.ohunelo.databinding.NotificationsHeaderItemBinding
import com.jesse.ohunelo.databinding.NotificationsItemBinding
import com.jesse.ohunelo.util.UiText

class NotificationsAdapter(
    private val onNotificationItemClicked: (notification: Notification) -> Unit
): ListAdapter<NotificationUIItem, RecyclerView.ViewHolder>(NotificationsDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.notifications_header_item -> NotificationsHeaderViewHolder.inflateFrom(parent)
            R.layout.notifications_item-> NotificationViewHolder.inflateFrom(parent)
            else -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            notificationUIItem ->
            when(holder){
                is NotificationsHeaderViewHolder -> {
                    holder.bind((notificationUIItem as NotificationUIItem.NotificationHeader).header)
                }
                is NotificationViewHolder -> {
                    val notificationItem = (notificationUIItem as NotificationUIItem.NotificationItem)
                    holder.bind(notification = notificationItem.item,
                        onNotificationItemClicked = onNotificationItemClicked
                    )
                }
                else -> throw UnsupportedOperationException("Unknown view")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is NotificationUIItem.NotificationHeader -> R.layout.notifications_header_item
            is NotificationUIItem.NotificationItem -> R.layout.notifications_item
            null -> throw IllegalStateException("Invalid new type")
        }
    }

    class NotificationsHeaderViewHolder(private val binding: NotificationsHeaderItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(headerUiText: UiText){
                binding.header = headerUiText

            }
            companion object {
                fun inflateFrom(parent: ViewGroup): NotificationsHeaderViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = NotificationsHeaderItemBinding.inflate(layoutInflater, parent,
                        false)
                    return NotificationsHeaderViewHolder(binding)
                }
            }
        }

    class NotificationViewHolder(private val binding: NotificationsItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(notification: Notification, onNotificationItemClicked: (notification: Notification) -> Unit){
                binding.apply {
                    setNotification(notification)
                    root.setOnClickListener {
                        onNotificationItemClicked(notification)
                    }
                    executePendingBindings()
                }
            }
            companion object {
                fun inflateFrom(parent: ViewGroup): NotificationViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = NotificationsItemBinding.inflate(layoutInflater, parent,
                        false)
                    return NotificationViewHolder(binding)
                }
            }
        }

    class NotificationsDiffUtil: DiffUtil.ItemCallback<NotificationUIItem>(){
        override fun areItemsTheSame(
            oldItem: NotificationUIItem,
            newItem: NotificationUIItem
        ): Boolean {
            val isSameNotificationHeaderItem = oldItem is NotificationUIItem.NotificationHeader
                    && newItem is NotificationUIItem.NotificationHeader && oldItem.header == newItem.header
            val isSameNotificationItem = oldItem is NotificationUIItem.NotificationItem
                    && newItem is NotificationUIItem.NotificationItem
                    && oldItem.item.id == newItem.item.id
            return isSameNotificationHeaderItem || isSameNotificationItem
        }

        override fun areContentsTheSame(
            oldItem: NotificationUIItem,
            newItem: NotificationUIItem
        ): Boolean {
            return newItem == oldItem
        }
    }

}