package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.OnboardingViewPagerAdapter
import com.jesse.ohunelo.databinding.FragmentOnboardingBinding


class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding: FragmentOnboardingBinding get() = _binding!!

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
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}