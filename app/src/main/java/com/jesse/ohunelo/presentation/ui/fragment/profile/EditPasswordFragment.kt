package com.jesse.ohunelo.presentation.ui.fragment.profile

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentEditPasswordBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.ReauthenticateEmailDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.EditPasswordViewModel
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPasswordFragment : Fragment() {

    private var _binding: FragmentEditPasswordBinding? = null
    private val binding: FragmentEditPasswordBinding get() = _binding!!

    private val viewModel by viewModels<EditPasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate<FragmentEditPasswordBinding>(
            inflater, R.layout.fragment_edit_password, container, false).apply {
                viewModel = this@EditPasswordFragment.viewModel
                lifecycleOwner = viewLifecycleOwner
                executePendingBindings()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editPasswordUiState.collectLatest {
                    editPasswordUiState ->
                    binding.progressBar.isVisible = editPasswordUiState.isLoading
                    // If there is a message to be shown, show it
                    editPasswordUiState.message?.let {
                            message ->
                        showMessage(message)
                        viewModel.onMessageShown()
                    }
                    if (editPasswordUiState.navigateBack){
                        findNavController().navigateUp()
                        viewModel.onNavigateBack()
                    }
                    if(editPasswordUiState.reauthenticate){
                        // Initiate re-authenticate
                        ReauthenticateEmailDialogFragment(
                            onDismiss = {
                                viewModel.renableButton()
                            },
                            onSuccess = {
                                viewModel.finishEditPassword()
                            }
                        ).show(childFragmentManager,
                            ReauthenticateEmailDialogFragment.TAG)
                        viewModel.onReauthenticateInitiated()
                    }
                }
            }
        }

    }

    private fun showMessage(message: UiText){
        Toast.makeText(requireContext(), message.asString(requireContext()), Toast.LENGTH_SHORT).show()
    }

    private fun setOnClickListeners(){
        binding.apply {
            editPasswordToolBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            updateButton.setOnClickListener {
                viewModel?.startEditPassword()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.enterPasswordEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onPasswordTextChanged(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}