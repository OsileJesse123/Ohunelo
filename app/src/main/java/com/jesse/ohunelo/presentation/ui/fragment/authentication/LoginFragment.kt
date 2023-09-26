package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.content.IntentSender
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentLoginBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.LoginViewModel
import com.jesse.ohunelo.util.HOME_FRAGMENT
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

    private var _oneTapClient: SignInClient? = null
    private val oneTapClient: SignInClient get() = _oneTapClient!!

    private var _signInRequest: BeginSignInRequest? = null
    private val signInRequest: BeginSignInRequest get() = _signInRequest!!

    private var _callbackManager: CallbackManager? = null
    private val callbackManager: CallbackManager get() = _callbackManager!!

    private var startActivityForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()){
            result ->
            try{
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null){
                    viewModel.finishSignInWithGoogle(idToken)
                } else{
                    Timber.e("ID token was null")
                }
            } catch (e: ApiException){
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Timber.e("One-tap dialog was closed.")
                        viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_cancelled, "Google"))
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        Timber.e("One-tap encountered a network error.")
                        // Try again or just ignore.
                        viewModel.onSignInFailed(UiText.StringResource(R.string.network_error_occured))
                    }
                    else -> {
                        Timber.e("Couldn't get credential from result." +
                                " (${e.localizedMessage})")
                        viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                    }
                }
            }
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

        // Initialize signInRequest
        _signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        // Initialize oneTapClient
        _oneTapClient = Identity.getSignInClient(requireContext())

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

    private fun startSignInWithGoogle(){
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener {
                result ->
                try {
                    startActivityForResultLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                } catch (e: IntentSender.SendIntentException){
                    Timber.e("Couldn't start one tap ui: ${e.localizedMessage}")
                    viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                }
            }
            .addOnFailureListener {
                e ->
                when(e){
                    is ApiException -> {
                        if (e.statusCode == 16){
                            Timber.e("Sign in with google failed,Exception: $e, Error Message: ${e.localizedMessage}")
                            viewModel.onSignInFailed(UiText.StringResource(R.string.caller_temporarily_blocked))
                        } else{
                            Timber.e("Sign in with google failed,Exception: $e, Error Message: ${e.localizedMessage}")
                            viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                        }
                    }
                    else -> {
                        Timber.e("Sign in with google failed,Exception: $e, Error Message: ${e.localizedMessage}")
                        viewModel.onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                    }
                }

            }
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
            viewModel.startSignIn()
            startSignInWithGoogle()
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
        _signInRequest = null
        _oneTapClient = null
        _callbackManager = null
        _binding = null
    }
}