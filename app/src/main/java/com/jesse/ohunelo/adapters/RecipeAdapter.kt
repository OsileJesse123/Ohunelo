package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jesse.ohunelo.adapters.viewholders.RecipeViewHolder
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.RandomsItemLayoutBinding
import com.jesse.ohunelo.databinding.RecipesByCategoryItemLayoutBinding
import com.jesse.ohunelo.util.RecipeViewHolderType

class RecipeAdapter(private val viewHolderType: RecipeViewHolderType,
                    private val onItemClicked:((recipe: Recipe) -> Unit)):
    ListAdapter<Recipe, RecipeViewHolder>(RecipeDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewHolderType){
            RecipeViewHolderType.RANDOM_RECIPE -> {
                val binding = RandomsItemLayoutBinding
                    .inflate(layoutInflater, parent, false)
                RecipeViewHolder.RandomRecipesViewHolder(binding, onItemClicked)
            }
            RecipeViewHolderType.RECIPE_BY_CATEGORY -> {
                val binding = RecipesByCategoryItemLayoutBinding
                    .inflate(layoutInflater, parent, false)
                RecipeViewHolder.RecipesByCategoryViewHolder(binding, onItemClicked)
            }
        }
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeDiffUtil: DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}