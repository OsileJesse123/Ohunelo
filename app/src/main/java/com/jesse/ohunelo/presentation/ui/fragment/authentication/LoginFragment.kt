package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentLoginBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.LoginViewModel
import com.jesse.ohunelo.util.HOME_FRAGMENT
import com.jesse.ohunelo.util.UPDATE_USERNAME_FRAGMENT
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.VERIFY_EMAIL_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    private var loader: LoaderDialogFragment? = null

    private var _callbackManager: CallbackManager? = null
    private val callbackManager: CallbackManager get() = _callbackManager!!

    private var startActivityForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()){
            result ->
            viewModel.finishSignInWithGoogle(result.data)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        // Inflate the layout for this fragment

        binding.apply {
            viewModel = this@LoginFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = LoaderDialogFragment()

        // Initialize callbackManager
        _callbackManager = CallbackManager.Factory.create()

        setOnClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginUiStateFlow.collect{
                    loginUiState ->
                    if (loginUiState.navigateToNextScreen.first){
                        // If user should be taken to the next screen
                        determineNavigationDestination(loginUiState.navigateToNextScreen.second)
                    }
                    if (loginUiState.showErrorMessage.first){
                        showErrorMessage(loginUiState.showErrorMessage.second)
                    }
                    // If all other views are not enabled, then the loader should be shown
                    if (!loginUiState.isEnabled){
                        showLoader()
                    }
                    // If all other views are enabled, then the loader should be hidden
                    else{
                        hideLoader()
                    }
                }
            }
        }
    }

    private fun determineNavigationDestination(destination: String) {
        when(destination){
            HOME_FRAGMENT -> {
                findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
            }
            VERIFY_EMAIL_FRAGMENT -> {
                findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToVerifyEmailFragment())
            }
            UPDATE_USERNAME_FRAGMENT -> {
                findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToUpdateUsernameFragment())
            }
        }
        viewModel.onNavigationToNextScreen()
    }

    private fun showErrorMessage(errorMessage: UiText?) {
        Toast.makeText(
            requireContext(),
            errorMessage?.asString(requireContext()),
            Toast.LENGTH_LONG
        ).show()
        viewModel.onErrorMessageShown()
    }

    private fun showLoader(){
        loader?.let {
            loader ->
            if(!loader.isAdded){
                loader.show(childFragmentManager, LoaderDialogFragment.TAG)
            }
        }
    }

    private fun hideLoader(){
        loader?.let {
                loader ->
            if(loader.isAdded){
                loader.dismiss()
            }
        }
    }

    private fun setOnTextChangedListeners(){
        binding.enterEmailAddressEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onEmailTextChanged(it.toString())
            }
        }
        binding.enterPasswordEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onPasswordTextChanged(it.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setOnTextChangedListeners()
    }

    private fun startSignInWithFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            callbackManager,
            listOf( "email", "public_profile")
        )
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel.finishSignInWithFacebook(result.accessToken.token)
            }

            override fun onCancel() {
                viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_cancelled, "Facebook"))
            }

            override fun onError(error: FacebookException) {
                Timber.e("Login with facebook failed, Error: ${error.message}")
                viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
            }
        })
    }

    private fun setOnClickListeners() {
        binding.loginButton.setOnClickListener {
            viewModel.login()
        }

        binding.continueWithGoogleButton.setOnClickListener {
            viewModel.startSignInWithGoogle {
                result ->
                startActivityForResultLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
            }
        }

        binding.continueWithFacebookButton.setOnClickListener {
            viewModel.startSignIn()
            startSignInWithFacebook()
        }

        binding.continueWithTwitterButton.setOnClickListener {
            viewModel.signInWithTwitter(requireActivity())
        }

        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
        }

        binding.registerText.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _callbackManager = null
        _binding = null
    }
}