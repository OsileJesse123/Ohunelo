package com.jesse.ohunelo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.Step
import com.jesse.ohunelo.databinding.InstructionsItemBinding

class InstructionsAdapter(private val onAnalyzedInstructionClicked: ((analyzedInstructions:
                                                               AnalyzedInstructions) -> Unit)):
    ListAdapter<AnalyzedInstructions, InstructionsAdapter.
InstructionsViewHolder>(InstructionsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsViewHolder =
        InstructionsViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: InstructionsViewHolder, position: Int) {
        holder.bind(getItem(position), onAnalyzedInstructionClicked)
    }

    class InstructionsViewHolder(private val binding: InstructionsItemBinding):
        ViewHolder(binding.root){

        private var analyzedInstructions: AnalyzedInstructions? = null

        companion object {
            fun inflateFrom(parent: ViewGroup): InstructionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InstructionsItemBinding.inflate(layoutInflater, parent, false)
                return InstructionsViewHolder(binding)
            }

        }

        fun bind(item: AnalyzedInstructions, onAnalyzedInstructionClicked: ((analyzedInstructions:
                                                                      AnalyzedInstructions) -> Unit)) {
            binding.apply {
                instruction = item
                root.setOnClickListener {
                    analyzedInstructions?.let {
                            analyzedInstructions -> onAnalyzedInstructionClicked(analyzedInstructions)
                    }
                }
                executePendingBindings()
            }
            analyzedInstructions = item
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