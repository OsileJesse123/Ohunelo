package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.SeeAllRecipesItemBinding

class SeeAllRecipesAdapter(
    private val onRecipeSelected: ((recipe: Recipe) -> Unit)
): PagingDataAdapter<Recipe,
        SeeAllRecipesAdapter.SeeAllRecipesViewHolder>(SeeAllRecipesDiffUtil()) {

    override fun onBindViewHolder(holder: SeeAllRecipesViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onRecipeSelected) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeAllRecipesViewHolder {
        return SeeAllRecipesViewHolder.inflateFrom(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount) {
            // Return a unique view type for LoadStateFooter item
            R.layout.see_all_recipes_load_state_footer_item
        } else {
            // Return the default view type for other items
            R.layout.see_all_recipes_item
        }
    }

    class SeeAllRecipesViewHolder(private val binding: SeeAllRecipesItemBinding):
        RecyclerView.ViewHolder(binding.root){

        private var selectedRecipe: Recipe? = null

        fun bind(recipe: Recipe, onRecipeSelected: ((recipe: Recipe) -> Unit)){
            binding.apply {
                setRecipe(recipe)
                root.setOnClickListener {
                    selectedRecipe?.let{
                        selectedRecipe ->
                        onRecipeSelected(selectedRecipe)
                    }
                }
                executePendingBindings()
            }
            selectedRecipe = recipe
        }

        companion object{

                fun inflateFrom(parent: ViewGroup): SeeAllRecipesViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = SeeAllRecipesItemBinding.inflate(layoutInflater, parent,
                        false)
                    return SeeAllRecipesViewHolder(binding)
                }
            }

        }

    class SeeAllRecipesDiffUtil: ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}