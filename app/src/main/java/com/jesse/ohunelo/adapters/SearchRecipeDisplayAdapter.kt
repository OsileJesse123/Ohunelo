package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.SearchRecipeDisplayItemBinding

class SearchRecipeDisplayAdapter(
    private val navigateToRecipeDetails: (recipe: Recipe) -> Unit
): PagingDataAdapter<Recipe,
        SearchRecipeDisplayAdapter.SearchRecipeDisplayViewHolder>(SearchRecipeDisplayDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRecipeDisplayViewHolder {
        return SearchRecipeDisplayViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: SearchRecipeDisplayViewHolder, position: Int) {
        getItem(position)?.let { recipe ->  holder.bind(recipe, navigateToRecipeDetails) }
    }

    class SearchRecipeDisplayViewHolder(private val binding: SearchRecipeDisplayItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(recipe: Recipe, navigateToRecipeDetails: (recipe: Recipe) -> Unit){
                binding.apply {
                    setRecipe(recipe)
                    binding.navigateImage.setOnClickListener {
                        navigateToRecipeDetails(recipe)
                    }
                    executePendingBindings()
                }
            }

            companion object{
                fun inflateFrom(parent: ViewGroup): SearchRecipeDisplayViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = SearchRecipeDisplayItemBinding.inflate(layoutInflater)
                    return SearchRecipeDisplayViewHolder(binding)
                }
            }
        }

    class SearchRecipeDisplayDiffUtil: DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}