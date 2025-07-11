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
import com.aura.ui.login.LoginActivity
import com.aura.ui.login.LoginUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import viewModel.transfer.TransferViewModel

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity()
{

  /**
   * The binding for the transfer layout.
   */
  private lateinit var binding: ActivityTransferBinding

  private val transferViewModel: TransferViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val transfer = binding.transfer
    val loading = binding.loading

    setupInputListeners()
    observeViewModel()

    transfer.setOnClickListener {
      loading.visibility = View.VISIBLE

      setResult(Activity.RESULT_OK)
      finish()
    }
  }

  private fun setupInputListeners() {
    binding.recipient.doOnTextChanged { text, _, _, _ ->
      transferViewModel.setRecipient(text.toString())
    }

    binding.amount.doOnTextChanged { text, _, _, _ ->
      transferViewModel.setAmount(text.toString())
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
      }
    }
  }

}
