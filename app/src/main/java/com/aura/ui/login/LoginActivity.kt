package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import viewModel.viewModel.LoginViewModel

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
      loginViewModel.setEmail(text.toString())
    }

    binding.password.doOnTextChanged { text, _, _, _ ->
      loginViewModel.setPassword(text.toString())
    }
  }

  /**
   * Observes the view model.
   */
  private fun observeViewModel() {
    Log.d("LoginActivity", "observeViewModel: Starting observation")
    lifecycleScope.launch {
      Log.d("LoginActivity", "observeViewModel: lifecycleScope.launch entered")
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        Log.d("LoginActivity", "observeViewModel: repeatOnLifecycle - STARTED")
        launch {
          loginViewModel.isLoginFormValid.collect { isEnabled ->
            binding.login.isEnabled = isEnabled
          }

          launch {
            Log.d("LoginActivity", "observeViewModel: Starting to collect loginState")
            loginViewModel.loginState.collect { state ->
              Log.i("LoginActivity", "Login $state")
              if (state.granted) {
                Log.d("LoginActivity", "State is granted, navigating to home...")
                navigateToHome()
              } else {
                Log.d("LoginActivity", "State is NOT granted.")
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
  private fun navigateToHome() {
    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
    startActivity(intent)
    finish()
  }
}
