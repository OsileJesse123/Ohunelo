package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.databinding.InstructionsItemLayoutBinding

class InstructionsAdapter: ListAdapter<AnalyzedInstructions, InstructionsAdapter.
InstructionsViewHolder>(InstructionsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = InstructionsItemLayoutBinding.inflate(layoutInflater, parent, false)
        return InstructionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstructionsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class InstructionsViewHolder(private val binding: InstructionsItemLayoutBinding):
        ViewHolder(binding.root){

        fun bind(item: AnalyzedInstructions) {
            binding.instruction = item
            binding.executePendingBindings()
        }

    }

    class InstructionsDiffUtil: DiffUtil.ItemCallback<AnalyzedInstructions>(){
        override fun areItemsTheSame(
            oldItem: AnalyzedInstructions,
            newItem: AnalyzedInstructions
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AnalyzedInstructions,
            newItem: AnalyzedInstructions
        ): Boolean {
            return oldItem == newItem
        }
    }
}