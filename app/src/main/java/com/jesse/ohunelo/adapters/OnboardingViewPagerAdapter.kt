package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.OnboardingScreenContentBinding
import com.jesse.ohunelo.presentation.ui.fragment.authentication.OnboardingFragment

class OnboardingViewPagerAdapter: RecyclerView.Adapter<OnboardingViewPagerAdapter.OnboardingViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewPagerViewHolder {
        return OnboardingViewPagerViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: OnboardingViewPagerViewHolder, position: Int) {
        holder.itemView.run {
            // DataBinding was not used in this case as it was overkill
            with(holder) {
                if (position == 0) {
                    binding.contentTitle.text = context.getString(R.string.content_title_1)
                    binding.contentDescription.text = context.getString(R.string.content_description_1)
                    binding.contentImage.setImageResource(R.drawable.onboarding_image_1)
                }
                if (position == 1) {
                    binding.contentTitle.text = context.getString(R.string.content_title_2)
                    binding.contentDescription.text = context.getString(R.string.content_description_2)
                    binding.contentImage.setImageResource(R.drawable.onboarding_image_2)
                }
                if (position == 2) {
                    binding.contentTitle.text = context.getString(R.string.content_title_3)
                    binding.contentDescription.text = context.getString(R.string.content_description_3)
                    binding.contentImage.setImageResource(R.drawable.onboarding_image_3)
                }
            }
        }
    }

    override fun getItemCount(): Int = OnboardingFragment.NUMBER_OF_SCREENS

    class OnboardingViewPagerViewHolder(val binding: OnboardingScreenContentBinding):
        RecyclerView.ViewHolder(binding.root){

        companion object{
            fun inflateFrom(parent: ViewGroup): OnboardingViewPagerViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OnboardingScreenContentBinding.inflate(layoutInflater, parent, false)
                return OnboardingViewPagerViewHolder(binding)
            }
        }
    }
}