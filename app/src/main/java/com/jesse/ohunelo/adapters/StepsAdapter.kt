package com.jesse.ohunelo.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.R.attr
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.jesse.ohunelo.data.network.models.Step
import com.jesse.ohunelo.databinding.StepsItemBinding

class StepsAdapter: ListAdapter<Step, StepsAdapter.StepsViewHolder> (StepsDiffUtil()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder =
        StepsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: StepsViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class StepsViewHolder(private val binding: StepsItemBinding): RecyclerView.ViewHolder(binding.root){

        companion object{
            fun inflateFrom(parent: ViewGroup): StepsViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StepsItemBinding.inflate(layoutInflater, parent,
                    false)
                return StepsViewHolder(binding)
            }
        }
        fun bind(item: Step, position: Int) {
            binding.step = item

            // Calculate the darker shade of the background color based on the position
            val backgroundColor = darkenColor(attr.colorPrimary,
                position * 0.04f, binding.root)

            binding.color = backgroundColor

            binding.executePendingBindings()
        }

        private fun darkenColor(colorRes: Int, factor: Float, view: View): Int {
            val colorInt = MaterialColors.getColor(view, colorRes)

            // We get the RGB value of the original color
            val red = Color.red(colorInt)
            val green = Color.green(colorInt)
            val blue = Color.blue(colorInt)

            /*
            * To calculate the darker shade of the color, we need to reduce the intensity of each
            * component. We do this by multiplying the original component value by a factor less
            * than 1. The factor determines the darkness level. A factor of 0 would result in a
            * completely black color, while a factor of 1 would keep the original color unchanged.
            *
            * We subtract the factor from 1 before performing the calculation. This adjustment
            * allows the factor to represent the darkness level more intuitively. A factor of 0.1
            *  would result in a slight darkening, while a factor of 0.9 would result in a
            * significant darkening.
            *
            * example: darkenedRed = 255 * (1 - 0.1) = 255 * 0.9 = 229, value of red reduces.
            * */
            val darkenedRed = (red * (1 - factor)).toInt()
            val darkenedGreen = (green * (1 - factor)).toInt()
            val darkenedBlue = (blue * (1 - factor)).toInt()

            return Color.rgb(darkenedRed, darkenedGreen, darkenedBlue)
        }
    }

    class StepsDiffUtil: DiffUtil.ItemCallback<Step>(){

        override fun areItemsTheSame(oldItem: Step, newItem: Step): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: Step, newItem: Step): Boolean {
            return oldItem == newItem
        }
    }
}