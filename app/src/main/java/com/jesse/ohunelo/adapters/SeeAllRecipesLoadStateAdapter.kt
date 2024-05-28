package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.SeeAllRecipesLoadStateFooterItemBinding
import com.jesse.ohunelo.util.NotFoundException
import com.jesse.ohunelo.util.RateLimitExceededException
import com.jesse.ohunelo.util.ServerErrorException
import com.jesse.ohunelo.util.UnauthorizedException

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
                    val resources = this.root.resources
                    if (loadState is LoadState.Error) {
                        val loadStateError = when(loadState.error){
                            is UnauthorizedException -> resources.getString(R.string.unauthorized_request)
                            is RateLimitExceededException -> resources.getString(R.string.rate_limit_exceeded)
                            is NotFoundException -> resources.getString(R.string.page_not_found)
                            is ServerErrorException -> resources.getString(R.string.internal_server_error)
                            else -> resources.getString(R.string.failed_to_get_recipes)
                        }
                        binding.errorMsg.text = loadStateError
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