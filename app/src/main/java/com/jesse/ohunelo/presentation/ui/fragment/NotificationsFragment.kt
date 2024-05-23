package com.jesse.ohunelo.presentation.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.NotificationHeaderAdapter
import com.jesse.ohunelo.adapters.NotificationsAdapter
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.databinding.FragmentNotificationsBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.NotificationExpandedItemDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.NotificationsViewModel
import com.jesse.ohunelo.util.PermissionRequest
import com.jesse.ohunelo.util.PermissionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding: FragmentNotificationsBinding get() = _binding!!

    private val viewModel by viewModels<NotificationsViewModel>()

    private var _notificationsAdapter: NotificationsAdapter? = null
    private val notificationsAdapter: NotificationsAdapter get() = _notificationsAdapter!!

    @SuppressLint("InlinedApi")
    // POST_NOTIFICATIONS is automatically granted on API<33.
    val permissionRequest = PermissionRequest(this, Manifest.permission.POST_NOTIFICATIONS)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container,
            false)

        _notificationsAdapter = NotificationsAdapter{
            notification ->  showNotificationDetailsInDialog(notification)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecycler()

        val notificationHeaderAdapter = NotificationHeaderAdapter { permissionRequest.launch() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    permissionRequest.status.collectLatest{
                            status ->
                        when(status){
                            is PermissionStatus.Granted -> binding.notificationsRecycler.adapter = notificationsAdapter
                            is PermissionStatus.Denied -> {
                                val concatAdapter = ConcatAdapter(notificationHeaderAdapter, notificationsAdapter)
                                binding.notificationsRecycler.adapter = concatAdapter
                                notificationHeaderAdapter.shouldShowRationale = status.shouldShowRationale
                            }
                            else -> {
                                // Do Nothing
                            }
                        }
                    }
                }
                launch {
                    viewModel.notifications.collectLatest {
                            groupedItems ->
                        /*if(groupedItems.isNotEmpty()){
                            notificationsAdapter.submitList(groupedItems.subList(0,7))
                        }*/
                        notificationsAdapter.submitList(groupedItems)
                        binding.noNotificationsText.isVisible = groupedItems.isEmpty()
                    }
                }
            }
        }

    }

    private fun setupRecycler(){
        binding.notificationsRecycler.apply {
            adapter = notificationsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,
                false)
        }
        //notificationsAdapter.submitList(viewModel.notifs)
    }

    private fun showNotificationDetailsInDialog(selectedNotification: Notification){
        NotificationExpandedItemDialogFragment(
            selectedNotification,
            onDismissNotification = {
                notification ->
                viewModel.updateNotification(notification)
            }
        ).show(
            childFragmentManager, NotificationExpandedItemDialogFragment.TAG
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _notificationsAdapter = null
        _binding = null
    }
}