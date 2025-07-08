package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import kotlinx.coroutines.launch
import viewModel.login.LoginViewModel

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

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupInputListeners()
    observeViewModel()
    setupLoginButton()
  }

  private fun setupInputListeners() {
    binding.identifier.doOnTextChanged { text, _, _, _ ->
      loginViewModel.setEmail(text.toString())
    }

    binding.password.doOnTextChanged { text, _, _, _ ->
      loginViewModel.setPassword(text.toString())
    }
  }

  private fun observeViewModel() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        loginViewModel.isLoginFormValid.collect { isEnabled ->
          binding.login.isEnabled = isEnabled
          binding.login.visibility = if (isEnabled) View.VISIBLE else View.INVISIBLE
        }
      }
    }
  }

  private fun setupLoginButton() {
    binding.login.setOnClickListener {

      if (binding.login.isEnabled) {
        navigateToHome()
      }
    }
  }

  private fun navigateToHome() {
    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
    startActivity(intent)
    finish()
  }
}
