package com.rakuseru.storyapp1.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.rakuseru.storyapp1.R
import com.rakuseru.storyapp1.data.remote.RequestRegister
import com.rakuseru.storyapp1.databinding.ActivityRegisterBinding
import com.rakuseru.storyapp1.ui.viewmodel.RegisterViewModel
import com.rakuseru.storyapp1.ui.viewmodel.ViewModelFactory
import com.rakuseru.storyapp1.utils.Result

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    //ViewModels
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var isPwdMatch: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setAction()
        setupObserver()
    }

    // Custom View Actions
    private fun setAction() {
        // Input Password
        binding.tiPass.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }
        binding.tiCpass.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }

        // Button Register
        binding.btnRegister.setOnClickListener {
            val name = binding.tiName.text.toString().trim()
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
                    val register = RequestRegister(name, email, password)
                    registerViewModel.register(register)
                }
            }
        }

        // See password check box
        binding.seePassword.setOnClickListener {
            if (binding.seePassword.isChecked) {
                binding.tiPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.tiCpass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.tiPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tiCpass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun setupObserver() {
        registerViewModel.registerResponse.observe(this){ registerResponse ->
            when(registerResponse) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> registerResponse.data.let {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.user_created), Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }

                is Result.Error -> registerResponse.data.let {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isPasswordMatch() {
        if (binding.tiPass.text.toString().trim() != binding.tiCpass.text.toString().trim()) {
            binding.tiCpass.error = resources.getString(R.string.pass_not_match)
            isPwdMatch = false
        } else {
            binding.tiCpass.error = null
            isPwdMatch = true
        }
    }

    // Skeleton Loading
    private fun showLoading(isLoading: Boolean) {
        val skeleton = findViewById<SkeletonLayout>(R.id.skeletonLayout)
        if (isLoading) skeleton.showSkeleton() else skeleton.showOriginal()
    }

}