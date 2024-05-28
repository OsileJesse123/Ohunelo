package com.jesse.ohunelo.presentation.ui.fragment.profile

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.signin_handlers.FacebookSignInHandler
import com.jesse.ohunelo.databinding.FragmentEditEmailBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.ReauthenticateEmailDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.EditEmailViewModel
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UserType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditEmailFragment : Fragment() {

    private var _binding: FragmentEditEmailBinding? = null
    private val binding: FragmentEditEmailBinding get() = _binding!!

    private val viewModel by viewModels<EditEmailViewModel>()


    @Inject
    lateinit var facebookSignInHandler: FacebookSignInHandler

    private var startActivityForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()){
            result ->
        viewModel.finishReauthenticateWithGoogle(result.data)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate<FragmentEditEmailBinding>(inflater, R.layout.fragment_edit_email, container, false).apply {
            viewModel = this@EditEmailFragment.viewModel
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
                viewModel.editEmailUiState.collectLatest {
                    editEmailUiState ->
                    binding.progressBar.isVisible = editEmailUiState.isLoading
                    // If there is a message to be shown, show it
                    editEmailUiState.message?.let {
                        message ->
                        showMessage(message)
                        viewModel.onMessageShown()
                    }
                    // If user should be re-authenticated
                    if (editEmailUiState.reauthenticate.first){
                        // Find out what method was used to login then initiate re-authentication
                        when(editEmailUiState.reauthenticate.second){
                            UserType.EMAIL_PASSWORD -> {
                                // Initiate re-authenticate
                                ReauthenticateEmailDialogFragment(
                                    onDismiss = {
                                    viewModel.renableButton()
                                }).show(childFragmentManager,
                                    ReauthenticateEmailDialogFragment.TAG)
                                viewModel.onReauthenticateInitiated()
                            }
                            UserType.GOOGLE -> {
                                viewModel.run {
                                    startReauthenticateWithGoogle {
                                            result ->
                                        startActivityForResultLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                                    }
                                    onReauthenticateInitiated()
                                }
                            }
                            UserType.FACEBOOK -> {
                                facebookSignInHandler.signIn(
                                    onSignInSuccess = {
                                            idToken ->
                                        viewModel.finishReauthenticateWithFacebook(idToken)
                                    },
                                    onSignInFailed = {
                                        viewModel.onFacebookReauthenticateFailed(UiText.StringResource(R.string.sign_in_cancelled, "Facebook"))
                                    },
                                    this@EditEmailFragment
                                )
                                viewModel.onReauthenticateInitiated()
                            }
                            UserType.TWITTER -> {
                                viewModel.run {
                                    reauthenticateTwitter(requireActivity())
                                    onReauthenticateInitiated()
                                }
                            }
                            else -> {
                                viewModel.onReauthenticateInitiated()
                                Toast.makeText(requireContext(),
                                    getString(R.string.unknown_user), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    // If user should be forcefully logged out
                    if(editEmailUiState.logout){
                        viewModel.onLogout()
                        findNavController().navigate(EditEmailFragmentDirections.actionEditEmailFragmentToLoginFragment())
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
            editEmailToolBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            updateButton.setOnClickListener {
                viewModel?.editEmail()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.enterEmailAddressEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onEmailTextChanged(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}