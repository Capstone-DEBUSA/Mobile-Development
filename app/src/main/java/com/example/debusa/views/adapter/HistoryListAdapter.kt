package com.example.debusa.views.adapter

import com.example.debusa.data.local.entity.HistoryAnalyzeEntity
import com.example.debusa.databinding.ItemHistoryBinding

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


class HistoryListAdapter(private val listener: OnItemClickListener): ListAdapter<HistoryAnalyzeEntity, HistoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: ItemHistoryBinding, private val listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryAnalyzeEntity){
            val imageUri = Uri.parse(history.image)
            binding.result.text = "${history.result}"
            Glide.with(itemView.context)
                .load(imageUri)
                .apply(RequestOptions().transform(RoundedCorners(50)))
                .into(binding.imageAnalyze)

            binding.root.setOnClickListener {
                val sharedElements = listOf(
                    Pair(binding.imageAnalyze, "hisImage"),
                    Pair(binding.result, "hisName"),
                )
                listener.onItemClick(history,sharedElements)
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, listener )
    }

    override fun onBindViewHolder(holder: HistoryListAdapter.MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    interface OnItemClickListener {
        fun onItemClick(item: HistoryAnalyzeEntity,sharedElements: List<Pair<View, String>>)
    }


    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryAnalyzeEntity>(){
            override fun areItemsTheSame(
                oldItem: HistoryAnalyzeEntity,
                newItem: HistoryAnalyzeEntity
            ): Boolean {
                return  oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HistoryAnalyzeEntity,
                newItem: HistoryAnalyzeEntity
            ): Boolean {
                return  oldItem == newItem
            }
        }
    }
}
