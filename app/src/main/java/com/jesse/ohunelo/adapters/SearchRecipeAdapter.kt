package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.SearchRecipeItemBinding

class SearchRecipeAdapter:
    PagingDataAdapter<Recipe, SearchRecipeAdapter.SearchRecipeViewHolder>(SearchRecipeDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecipeViewHolder {
        return SearchRecipeViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: SearchRecipeViewHolder, position: Int) {
        getItem(position)?.let {
            recipe ->
            holder.bind(recipe) }
    }

    class SearchRecipeViewHolder(private val binding: SearchRecipeItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(recipe: Recipe){
                binding.apply {
                    setRecipe(recipe)
                    executePendingBindings()
                }
            }

            companion object{
                fun inflateFrom(parent: ViewGroup): SearchRecipeViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = SearchRecipeItemBinding.inflate(layoutInflater)
                    return SearchRecipeViewHolder(binding)
                }
            }

        }

    class SearchRecipeDiffUtil: DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}