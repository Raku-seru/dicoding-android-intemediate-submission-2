package com.rakuseru.storyapp1.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.snackbar.Snackbar
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.data.remote.RequestLogin
import com.rakuseru.storyapp1.databinding.ActivityMainBinding
import com.rakuseru.storyapp1.ui.viewmodel.MainViewModel
import com.rakuseru.storyapp1.ui.viewmodel.UserViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory
import com.rakuseru.storyapp1.utils.Result

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    // Skeleton View Init
    private lateinit var skEmail: Skeleton
    private lateinit var skPass: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Init activity functions
        setAction()
        setObservers()

        // Skeleton View Create
        skEmail = binding.tiEmail.createSkeleton()
        skPass = binding.tiPass.createSkeleton()
    }

    // Custom View Actions
    private fun setAction() {
        // Login Button
        binding.btnLogin.setOnClickListener {
            val email = binding.tiEmail.text.toString().trim()
            val password = binding.tiPass.text.toString().trim()
            when {
                email.isEmpty() -> {
                    binding.tiEmail.error = getString(R.string.email_required)
                }
                password.length < 6 -> {
                    binding.tiPass.error = getString(R.string.pass_length)
                }
                else -> {
                    val login = RequestLogin(email, password)
                    mainViewModel.login(login)
                }
            }
        }

        // SignUp / Register button
        binding.signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // See password checkbox
        binding.seePassword.setOnClickListener {
            if (binding.seePassword.isChecked) {
                binding.tiPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.tiPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

    }

    // Observer Listener
    private fun setObservers() {
        mainViewModel.resLogin.observe(this) { res ->
            when (res) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> res.data?.loginResult?.let {
                    showLoading(false)
                    mainViewModel.saveUser(it.token)
                    Toast.makeText(this, getString(R.string.success_login, it.name), Toast.LENGTH_LONG).show()

                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    intent.putExtra(HomeActivity.EXTRA_TOKEN, it.token)
                    startActivity(intent)
                    finish() // Login Activity closed
                }
                is Result.Error -> res.data.let {
                    showLoading(false)
                    Snackbar.make(binding.root, getString(R.string.unauthorized), Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    // Skeleton loading
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            skEmail.showSkeleton()
            skPass.showSkeleton()
        } else {
            skEmail.showOriginal()
            skPass.showOriginal()
        }
    }

}