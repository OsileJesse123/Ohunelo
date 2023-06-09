package com.jesse.ohunelo.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jesse.ohunelo.adapters.viewholders.RecipeViewHolder
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.util.RecipeViewHolderType

class RecipeAdapter(private val viewHolderType: RecipeViewHolderType,
                    private val onItemClicked:((recipe: Recipe) -> Unit)):
    ListAdapter<Recipe, RecipeViewHolder>(RecipeDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return when(viewHolderType){
            RecipeViewHolderType.RANDOM_RECIPE -> {
                RecipeViewHolder.RandomRecipesViewHolder.inflateFrom(parent)
            }
            RecipeViewHolderType.RECIPE_BY_CATEGORY -> {
                RecipeViewHolder.RecipesByCategoryViewHolder.inflateFrom(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
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