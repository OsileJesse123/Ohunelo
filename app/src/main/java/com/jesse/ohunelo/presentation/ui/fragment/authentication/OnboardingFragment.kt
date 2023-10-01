package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.OnboardingViewPagerAdapter
import com.jesse.ohunelo.databinding.FragmentOnboardingBinding
import com.jesse.ohunelo.presentation.viewmodels.OnboardingViewModel
import com.jesse.ohunelo.presentation.viewmodels.SharedViewModel
import com.jesse.ohunelo.util.HOME_FRAGMENT
import com.jesse.ohunelo.util.LOGIN_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding: FragmentOnboardingBinding get() = _binding!!

    private val viewModel by viewModels<OnboardingViewModel>()

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    companion object{
        const val NUMBER_OF_SCREENS = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding, container,
            false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.onboardingViewPager.adapter = OnboardingViewPagerAdapter()

        TabLayoutMediator(binding.onboardingTabLayout, binding.onboardingViewPager){_,_ ->}.attach()

        binding.startCookingButton.setOnClickListener {
            viewModel.startCooking()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.onboardingUiStateFlow.collect{
                    onboaridingUiState ->
                    if (!onboaridingUiState.shouldKeepSplashScreenOn){
                        sharedViewModel.stopShowingSplashScreen()
                    }
                    if(onboaridingUiState.navigateToNextScreen.first){
                        determineNavigationDestination(onboaridingUiState.navigateToNextScreen.second)
                    }
                }
            }
        }
    }

    private fun determineNavigationDestination(navigationDestination: String){
        when(navigationDestination){
            HOME_FRAGMENT -> {
                findNavController().apply {
                    navigate(OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment())
                }
            }
            LOGIN_FRAGMENT -> {
                findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}