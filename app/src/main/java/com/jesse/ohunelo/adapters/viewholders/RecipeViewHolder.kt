package com.jesse.ohunelo.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.RandomsItemLayoutBinding
import com.jesse.ohunelo.databinding.RecipesByCategoryItemLayoutBinding

abstract class RecipeViewHolder(binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {

    class RandomRecipesViewHolder(private val binding: RandomsItemLayoutBinding,
                                  onItemClicked: ((recipe: Recipe) -> Unit)):
        RecipeViewHolder(binding){
        init {
            binding.root.setOnClickListener {
                binding.recipe?.let {
                  onItemClicked(it)
                }
            }
        }
        override fun bind(recipe: Recipe) {
            binding.recipe = recipe
            binding.executePendingBindings()
        }
    }

    class RecipesByCategoryViewHolder(private val binding: RecipesByCategoryItemLayoutBinding,
                                      onItemClicked: ((recipe: Recipe) -> Unit)):
        RecipeViewHolder(binding){
        init {
            binding.root.setOnClickListener {
                binding.recipe?.let {
                    onItemClicked(it)
                }
            }
        }
        override fun bind(recipe: Recipe) {
            binding.recipe = recipe
            binding.executePendingBindings()
        }
    }

    abstract fun bind(recipe: Recipe)
}