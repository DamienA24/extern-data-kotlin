package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import kotlinx.coroutines.launch
import viewModel.viewModel.LoginViewModel

/**
 * The login activity for the app.
 */
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
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        loginViewModel.isLoginFormValid.collect { isEnabled ->
          binding.login.isEnabled = isEnabled
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
        navigateToHome()
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
