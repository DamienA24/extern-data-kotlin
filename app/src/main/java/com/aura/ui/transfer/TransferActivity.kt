package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityTransferBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import viewModel.transfer.TransferViewModel

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity()
{

  companion object {
    private const val TAG = "TransferActivity"
  }
  /**
   * The binding for the transfer layout.
   */
  private lateinit var binding: ActivityTransferBinding
  private var currentSenderId: String? = null

  private val transferViewModel: TransferViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    currentSenderId = intent.getStringExtra("USER_ID_EXTRA")

    if (currentSenderId == null) {
      Log.e(TAG, "Sender ID is null. Cannot proceed with transfer.")
      Toast.makeText(this, "Sender ID is null", Toast.LENGTH_LONG).show()
      setResult(Activity.RESULT_CANCELED)
      finish()
      return
    }

    setupInputListeners()
    setupTransferButton()
    observeViewModel()

  }

  private fun setupInputListeners() {
    binding.recipient.doOnTextChanged { text, _, _, _ ->
      transferViewModel.setRecipient(text.toString())
    }

    binding.amount.doOnTextChanged { text, _, _, _ ->
      transferViewModel.setAmount(text.toString())
    }
  }

  private fun setupTransferButton() {
    binding.transfer.setOnClickListener {
      if (currentSenderId != null) {
        transferViewModel.transferAmount(currentSenderId!!)
      } else {
        Toast.makeText(this, "SenderId is null", Toast.LENGTH_SHORT).show()
        Log.e(TAG, "Transfer button clicked but senderId is null.")
      }
    }
  }

  /**
   * Observes the view model.
   */
  private fun observeViewModel() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          transferViewModel.isTransferFormValid.collect { isEnabled ->
            binding.transfer.isEnabled = isEnabled
          }
        }

        launch {
          transferViewModel.transferAmountUiState.collect { state ->
            Log.i(TAG, "TransferUiState observed: $state")
            binding.transfer.isEnabled =
              state !is TransferUiState.Loading && transferViewModel.isTransferFormValid.value

            when (state) {
              is TransferUiState.Idle -> {
                binding.loading.visibility = View.GONE
              }
              is TransferUiState.Loading -> {
                binding.loading.visibility = View.VISIBLE
              }
              is TransferUiState.Success -> {
                binding.loading.visibility = View.GONE
                Toast.makeText(
                  this@TransferActivity,
                  "Transfer successful",
                  Toast.LENGTH_LONG
                ).show()
                Log.i(TAG, "Transfer successful. Response: ${state.message}")
                setResult(Activity.RESULT_OK)
                finish()
              }
              is TransferUiState.Error -> {
                binding.loading.visibility = View.GONE
                Toast.makeText(this@TransferActivity, state.message, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Transfer error: ${state.message}")
              }
            }
          }
        }
      }
    }
  }

}
