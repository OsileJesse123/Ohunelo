package com.jesse.ohunelo.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.RandomsItemLayoutBinding
import com.jesse.ohunelo.databinding.RecipesByCategoryItemLayoutBinding

abstract class RecipeViewHolder(binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {

    class RandomRecipesViewHolder(private val binding: RandomsItemLayoutBinding,
                                  ):
        RecipeViewHolder(binding){

        companion object {
            fun inflateFrom(parent: ViewGroup): RandomRecipesViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RandomsItemLayoutBinding
                    .inflate(layoutInflater, parent, false)
                return RandomRecipesViewHolder(binding)
            }
        }
        override fun bind(recipe: Recipe, onItemClicked: ((recipe: Recipe) -> Unit)) {
            binding.apply {
                setRecipe(recipe)
                root.setOnClickListener {
                    binding.recipe?.let {
                        onItemClicked(it)
                    }
                }
                executePendingBindings()
            }
        }
    }

    class RecipesByCategoryViewHolder(private val binding: RecipesByCategoryItemLayoutBinding):
        RecipeViewHolder(binding){

        companion object {
            fun inflateFrom(parent: ViewGroup): RecipesByCategoryViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesByCategoryItemLayoutBinding
                    .inflate(layoutInflater, parent, false)
                return RecipesByCategoryViewHolder(binding)
            }
        }
        override fun bind(recipe: Recipe, onItemClicked: ((recipe: Recipe) -> Unit)) {
            binding.apply {
                setRecipe(recipe)
                root.setOnClickListener {
                    binding.recipe?.let {
                        onItemClicked(it)
                    }
                }
                executePendingBindings()
            }
        }
    }

    abstract fun bind(recipe: Recipe, onItemClicked: ((recipe: Recipe) -> Unit))
}