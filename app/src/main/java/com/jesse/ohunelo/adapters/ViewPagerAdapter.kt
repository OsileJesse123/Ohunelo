package com.jesse.ohunelo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle,
                        private val fragments: List<Fragment>): FragmentStateAdapter(fragmentManager,
    lifecycle) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> fragments[position]
            1 -> fragments[position]
            2 -> fragments[position]
            else -> fragments[position]
        }
    }


}