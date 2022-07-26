package com.rakuseru.storyapp1.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rakuseru.storyapp1.data.local.StoryEntity
import com.rakuseru.storyapp1.data.remote.ListStory
import com.rakuseru.storyapp1.databinding.ItemListStoryBinding
import com.rakuseru.storyapp1.ui.DetailActivity

class ListStoryAdapter:
    PagingDataAdapter<StoryEntity, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bind(item)
        }
    }

    // ViewHolder
    inner class ListViewHolder(private var binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind (itemData: StoryEntity) {
            binding.apply {
                tvItemName.text = itemData.name
                Glide.with(itemView.context)
                    .load(itemData.photoUrl)
                    .into(ivItemPhoto)

                ivItemPhoto.setOnClickListener {
                    val intent = Intent(it.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_STORY, itemData)
                    it.context.startActivity(
                        intent,
                        ActivityOptionsCompat
                            .makeSceneTransitionAnimation(it.context as Activity)
                            .toBundle()
                    )
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}