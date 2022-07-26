package com.rakuseru.storyapp1.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.databinding.ActivityHomeBinding
import com.rakuseru.storyapp1.ui.adapter.ListStoryAdapter
import com.rakuseru.storyapp1.ui.adapter.LoadingStateAdapter
import com.rakuseru.storyapp1.ui.viewmodel.HomeViewModel
import com.rakuseru.storyapp1.ui.viewmodel.UserViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this@HomeActivity)
    }
    private lateinit var token: String

    private lateinit var storyAdapter: ListStoryAdapter
    private lateinit var rvSkeleton: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(EXTRA_TOKEN).toString()

        // RecyclerView
        binding.rvStories.layoutManager = LinearLayoutManager(this@HomeActivity)

        // Init functions
        setAction()
        getData()

    }

    override fun onRestart() {
        super.onRestart()
        getData()
        storyAdapter.refresh()
    }

    private fun getData() {
        storyAdapter = ListStoryAdapter()

        homeViewModel.getStories(token).observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }

        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
    }

    // CustomView Actions
    private fun setAction() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        binding.btnRefresh.setOnClickListener {
            getData()
            storyAdapter.refresh()
            binding.rvStories.layoutManager?.scrollToPosition(0)
        }

        // Skeleton Init
        rvSkeleton = binding.rvStories.applySkeleton(R.layout.item_list_story)
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.menu_logout -> {
                showAlertDialog()
                true
            }
            R.id.menu_about -> {
                Toast.makeText(this, R.string.about_detail, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_location -> {
                val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                intent.putExtra(MapsActivity.TOKEN, token)
                startActivity(intent)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.you_sure))
            .setPositiveButton(getString(R.string.no)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logout()
            }
            .show()
    }

    private fun logout() {
        homeViewModel.deleteUser()
        Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) rvSkeleton.showSkeleton() else rvSkeleton.showOriginal()
    }

    private fun showNoData(isNoData: Boolean) {
        binding.tvHomeNoData.visibility = if (isNoData) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}