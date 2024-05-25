package com.deeon.submission_story_inter.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.databinding.ItemStoryFeedBinding
import com.deeon.submission_story_inter.view.StoryDetailActivity
import androidx.core.util.Pair as UtilPair

class StoryFeedAdapter :
    PagingDataAdapter<StoryDetail, StoryFeedAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val binding: ItemStoryFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryDetail) {
            with(binding) {
                Glide.with(binding.root)
                    .load(story.storyImgUrl)
                    .into(binding.ivItemPhoto)
                tvItemName.text = story.storyOwnerName
                tvStoryDescription.text = story.storyDescription
            }

            binding.storyCard.setOnClickListener {
                Intent(binding.storyCard.context, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailActivity.EXTRA_STORY_DETAIL, story)
                }.run {
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.storyCard.context as Activity,
                        UtilPair(binding.ivItemPhoto, "story_image"),
                        UtilPair(binding.tvItemName, "story_uploader"),
                        UtilPair(binding.tvStoryDescription, "story_description")
                    )

                    binding.storyCard.context.startActivity(this, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStoryFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryDetail>() {
            override fun areItemsTheSame(oldItem: StoryDetail, newItem: StoryDetail): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryDetail, newItem: StoryDetail): Boolean {
                return oldItem.storyId == newItem.storyId
            }
        }
    }

}