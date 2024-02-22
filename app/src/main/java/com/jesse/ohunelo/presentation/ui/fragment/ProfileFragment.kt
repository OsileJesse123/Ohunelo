package com.jesse.ohunelo.presentation.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentProfileBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LogoutDialogFragment
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.UpdateProfileDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_profile, container, false)

        binding.apply {
            viewModel = this@ProfileFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.apply {
            logOutCard.setOnClickListener {
                showLogoutDialog()
            }
            editProfileIcon.setOnClickListener {
                showUpdateProfileDialog()
            }
        }
    }

    private fun showUpdateProfileDialog(){
        UpdateProfileDialogFragment().show(childFragmentManager, UpdateProfileDialogFragment.TAG)
    }

    private fun showLogoutDialog(){
        LogoutDialogFragment(
            confirmOnClickListener = {
                viewModel.logout()
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
            }
        ).show(childFragmentManager, LogoutDialogFragment.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}