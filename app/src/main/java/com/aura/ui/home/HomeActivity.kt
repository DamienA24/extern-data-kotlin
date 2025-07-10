package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import viewModel.home.HomeViewModel


/**
 * The home activity for the app.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity()
{

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  private companion object {
    private const val TAG = "HomeActivity"
  }

  private val homeViewModel: HomeViewModel by viewModels()

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      //TODO
    }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val userId = intent.getStringExtra("USER_ID_EXTRA")
    if(userId == null) {
      Toast.makeText(this, "Error: User ID is null.", Toast.LENGTH_LONG).show()
      navigateToLogin()
      return
    }

    homeViewModel.loadUserData(userId)

    val transfer = binding.transfer
    val retry = binding.retryButton

    transfer.setOnClickListener {
      startTransferActivityForResult.launch(Intent(this@HomeActivity, TransferActivity::class.java))
    }
    retry.setOnClickListener {
      homeViewModel.loadUserData(userId)
    }

    observeHomeUiState()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean
  {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      R.id.disconnect ->
      {
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun observeHomeUiState() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        homeViewModel.homeUiState.collect { state ->
          Log.i(TAG, "HomeUiState collected: $state")

          when (state) {
            is HomeUiState.Idle -> {
              binding.balance.text = "..."
              binding.homeProgressBar.visibility = View.GONE
              binding.retryButton.visibility = View.GONE
              Log.d(TAG, "UI State: Idle")
            }
            is HomeUiState.Loading -> {
              binding.balance.text = "Loading..."
              binding.homeProgressBar.visibility = View.VISIBLE
              binding.retryButton.visibility = View.GONE
              Log.d(TAG, "UI State: Loading")
            }
            is HomeUiState.Success -> {
              binding.balance.text = homeViewModel.formatBalanceToString(state.userAccountData)
              binding.homeProgressBar.visibility = View.GONE
              binding.retryButton.visibility = View.GONE
              Log.d(TAG, "UI State: Success - Balance: ${binding.balance.text}")
            }
            is HomeUiState.Error -> {
              binding.balance.text = "--,--€"
              binding.homeProgressBar.visibility = View.GONE
              binding.retryButton.visibility = View.VISIBLE
              Toast.makeText(this@HomeActivity, state.message, Toast.LENGTH_SHORT).show()
              Log.w(TAG, "UI State: Error - ${state.message}")
            }
          }
        }
      }
    }
  }

  private fun navigateToLogin() {
    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
  }
}
