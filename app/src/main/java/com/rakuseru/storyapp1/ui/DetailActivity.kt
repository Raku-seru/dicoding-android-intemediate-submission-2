package com.rakuseru.storyapp1.ui

import com.rakuseru.storyapp1.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rakuseru.storyapp1.data.local.StoryEntity
import com.rakuseru.storyapp1.data.remote.ListStory
import com.rakuseru.storyapp1.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get parcel and show detail
        val extra = intent.getParcelableExtra<StoryEntity>(EXTRA_STORY)!!
        setStory(extra)
    }

    private fun setStory(story: StoryEntity) {
        val oldDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(story.createdAt)
        val newDate = SimpleDateFormat("yyyy-MM-dd").format(oldDate!!)

        binding.apply {
            tvDetailName.text = story.name
            tvDetailDate.text = newDate
            tvDetailDesc.text = story.description
        }

        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivDetailImg)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                supportFinishAfterTransition()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}