package com.rakuseru.storyapp1.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rakuseru.storyapp1.databinding.ActivitySplashScreenBinding
import com.rakuseru.storyapp1.ui.viewmodel.MainViewModel
import com.rakuseru.storyapp1.ui.viewmodel.UserViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private val mainViewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Init ViewModel
        checkLogin()
    }

    private fun checkLogin() {
        mainViewModel.fetchUser().observe(this) { token ->
            if (token != "") {
                startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java)
                    .putExtra(HomeActivity.EXTRA_TOKEN, token))
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}