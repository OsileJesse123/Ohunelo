package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.databinding.IngredientsItemLayoutBinding

class IngredientAdapter: ListAdapter<ExtendedIngredient, IngredientAdapter.IngredientViewHolder>(IngredientDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = IngredientsItemLayoutBinding.inflate(layoutInflater, parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class IngredientViewHolder(private val binding: IngredientsItemLayoutBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: ExtendedIngredient) {
            binding.ingredient = item
            binding.executePendingBindings()
        }

    }

    class IngredientDiffUtil: DiffUtil.ItemCallback<ExtendedIngredient>(){
        override fun areItemsTheSame(
            oldItem: ExtendedIngredient,
            newItem: ExtendedIngredient
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ExtendedIngredient,
            newItem: ExtendedIngredient
        ): Boolean {
            return oldItem == newItem
        }
    }
}