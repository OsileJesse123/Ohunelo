package com.jesse.ohunelo.presentation.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.NotificationsAdapter
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.databinding.FragmentNotificationsBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.NotificationExpandedItemDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding: FragmentNotificationsBinding get() = _binding!!

    private val viewModel by viewModels<NotificationsViewModel>()

    private var _notificationsAdapter: NotificationsAdapter? = null
    private val notificationsAdapter: NotificationsAdapter get() = _notificationsAdapter!!

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.notificationItems.collect{
                    groupedItems ->
                    Timber.e("GroupedItems: $groupedItems")
                    notificationsAdapter.submitList(groupedItems)
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
    }

    private fun showNotificationDetailsInDialog(selectedNotification: Notification){
        NotificationExpandedItemDialogFragment(selectedNotification).show(
            childFragmentManager, NotificationExpandedItemDialogFragment.TAG
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _notificationsAdapter = null
        _binding = null
    }
}