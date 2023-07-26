package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.databinding.NotificationsHeaderItemBinding
import com.jesse.ohunelo.databinding.NotificationsItemBinding
import com.jesse.ohunelo.util.UiText

class NotificationsAdapter(
    private val onNotificationItemClicked: (notification: Notification) -> Unit
): ListAdapter<GroupedNotificationItem,
        RecyclerView.ViewHolder>(NotificationsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_HEADER -> {
                NotificationsHeaderViewHolder.inflateFrom(parent)
            }
            else -> {
                NotificationViewHolder.inflateFrom(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder){
            is NotificationsHeaderViewHolder -> item.header?.let { header -> holder.bind(header) }
            is NotificationViewHolder -> item.notification?.let { notification ->  holder
                .bind(notification, onNotificationItemClicked) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || getItem(position).isHeader()) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_NOTIFICATION
        }
    }

    class NotificationsHeaderViewHolder(private val binding: NotificationsHeaderItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(header: UiText){
                binding.apply {
                    setHeader(header)
                    executePendingBindings()
                }
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

    class NotificationsDiffUtil: DiffUtil.ItemCallback<GroupedNotificationItem>(){
        override fun areItemsTheSame(
            oldItem: GroupedNotificationItem,
            newItem: GroupedNotificationItem
        ): Boolean {
            return newItem.header == oldItem.header && newItem.notification == oldItem.notification
        }

        override fun areContentsTheSame(
            oldItem: GroupedNotificationItem,
            newItem: GroupedNotificationItem
        ): Boolean {
            return newItem == oldItem
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_NOTIFICATION = 1
    }

}