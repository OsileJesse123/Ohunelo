package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.databinding.SeeAllRecipesLoadStateFooterItemBinding
import timber.log.Timber

class SeeAllRecipesLoadStateAdapter(private val retry: () -> Unit):  LoadStateAdapter<SeeAllRecipesLoadStateAdapter
.SeeAllRecipesLoadStateViewHolder>(){

    override fun onBindViewHolder(holder: SeeAllRecipesLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState, retry)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): SeeAllRecipesLoadStateViewHolder {
        return SeeAllRecipesLoadStateViewHolder.inflateFrom(parent)
    }

    class SeeAllRecipesLoadStateViewHolder(
        private val binding: SeeAllRecipesLoadStateFooterItemBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(loadState: LoadState, retry: () -> Unit){
                binding.apply {
                    if (loadState is LoadState.Error) {
                        binding.errorMsg.text = loadState.error.localizedMessage
                    }
                    loadingProgressBar.isVisible = loadState is LoadState.Loading
                    errorMsg.isVisible = loadState is LoadState.Error
                    retryButton.isVisible = loadState !is LoadState.Loading
                    retryButton.setOnClickListener {
                        retry()
                    }
                    executePendingBindings()
                }
            }

            companion object{
                fun inflateFrom(parent: ViewGroup): SeeAllRecipesLoadStateViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = SeeAllRecipesLoadStateFooterItemBinding
                        .inflate(layoutInflater, parent, false)
                    return SeeAllRecipesLoadStateViewHolder(binding)
                }
            }
        }

}