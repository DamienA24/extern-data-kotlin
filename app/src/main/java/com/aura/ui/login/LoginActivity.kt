package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import viewModel.login.LoginViewModel

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity()
{

  /**
   * The binding for the login layout.
   */
  private lateinit var binding: ActivityLoginBinding
  /**
   * The view model for the login activity.
   */
  private val loginViewModel: LoginViewModel by viewModels()

  /**
   * Called when the activity is created.
   * @param savedInstanceState The saved instance state.
   */
  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupInputListeners()
    observeViewModel()
    setupLoginButton()
  }

  /**
   * Sets up the input listeners.
   */
  private fun setupInputListeners() {
    binding.identifier.doOnTextChanged { text, _, _, _ ->
      loginViewModel.setId(text.toString())
    }

    binding.password.doOnTextChanged { text, _, _, _ ->
      loginViewModel.setPassword(text.toString())
    }
  }

  /**
   * Observes the view model.
   */
  private fun observeViewModel() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          loginViewModel.isLoginFormValid.collect { isEnabled ->
            binding.login.isEnabled = isEnabled
          }
        }
        launch {
          loginViewModel.loginUiState.collect { state ->
            Log.d("state", state.toString())
            when (state) {
              is LoginUiState.Idle -> {
                binding.progressBar.visibility = View.GONE
                binding.login.isEnabled = loginViewModel.isLoginFormValid.value
              }
              is LoginUiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.login.isEnabled = false
                binding.identifier.isEnabled = false
                binding.password.isEnabled = false
              }
              is LoginUiState.Success -> {
                binding.progressBar.visibility = View.GONE
                val userId = loginViewModel.userId.value
                navigateToHome(userId)
              }
              is LoginUiState.Error -> {
                Log.d("error", state.message)
                binding.progressBar.visibility = View.GONE
                binding.login.isEnabled = loginViewModel.isLoginFormValid.value
                binding.identifier.isEnabled = true
                binding.password.isEnabled = true
                Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
              }
            }
          }
        }
      }
    }
  }

  /**
   * Sets up the login button.
   */
  private fun setupLoginButton() {
    binding.login.setOnClickListener {

      if (binding.login.isEnabled) {
        loginViewModel.loginUser()
      }
    }
  }

  /**
   * Navigates to the home activity.
   */
  private fun navigateToHome(userId: String) {
    Log.d("userId", userId)
    val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
      putExtra("USER_ID_EXTRA", userId)
    }
    startActivity(intent)
    finish()
  }
}
