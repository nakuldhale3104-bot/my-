package com.jewelpromo.app.ui.scratch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jewelpromo.app.data.api.NetworkModule
import com.jewelpromo.app.data.repository.PromoRepository
import com.jewelpromo.app.databinding.ActivityScratchCardBinding
import com.jewelpromo.app.ui.success.SuccessActivity
import kotlinx.coroutines.launch

class ScratchCardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_CHANCES = "extra_chances"
    }

    private lateinit var binding: ActivityScratchCardBinding
    private var userId: Int = -1
    private var customerName: String = ""
    private var age: Int = 0
    private lateinit var chances: List<Int>
    private var latestRevealedDiscount = 0

    private val viewModel: ScratchViewModel by viewModels {
        val repo = PromoRepository(NetworkModule.promoApiService)
        ScratchViewModelFactory(repo, chances)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = intent.getIntExtra(EXTRA_USER_ID, -1)
        customerName = intent.getStringExtra(EXTRA_NAME).orEmpty()
        age = intent.getIntExtra(EXTRA_AGE, 0)
        chances = intent.getIntegerArrayListExtra(EXTRA_CHANCES)?.toList().orEmpty()

        if (userId < 0 || age <= 0 || chances.size != 3) {
            Toast.makeText(this, "Invalid game data", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding = ActivityScratchCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scratchOverlay.setOnRevealListener {
            onCardScratched()
        }

        observeUiState()
        renderAttempt(1)
    }

    private fun onCardScratched() {
        latestRevealedDiscount = viewModel.revealCurrentChance()
        binding.tvHiddenDiscount.text = "$latestRevealedDiscount%"

        when (viewModel.uiState.value.attempt) {
            1 -> showChoiceDialog(
                title = "You won $latestRevealedDiscount%!",
                message = "Lock this in, or risk it for Chance 2? You will lose this discount if you continue.",
                isFinalChoice = false,
            )

            2 -> showChoiceDialog(
                title = "You won $latestRevealedDiscount%!",
                message = "Lock it now, or take your final Chance 3?",
                isFinalChoice = false,
            )

            3 -> {
                showFinalDialogAndSave(latestRevealedDiscount)
            }
        }
    }

    private fun showChoiceDialog(title: String, message: String, isFinalChoice: Boolean) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Lock It") { _, _ ->
                viewModel.lockDiscount(userId, latestRevealedDiscount)
            }
            .setNegativeButton(if (isFinalChoice) "Finish" else "Risk It") { _, _ ->
                viewModel.riskNextChance()
                binding.tvHiddenDiscount.text = "?%"
                binding.scratchOverlay.resetScratch()
                renderAttempt(viewModel.uiState.value.attempt)
            }
            .show()
    }

    private fun showFinalDialogAndSave(finalDiscount: Int) {
        AlertDialog.Builder(this)
            .setTitle("Final Result")
            .setMessage("Final result! You won $finalDiscount%!")
            .setCancelable(false)
            .setPositiveButton("Continue") { _, _ ->
                viewModel.lockDiscount(userId, finalDiscount)
            }
            .show()
    }

    private fun renderAttempt(attempt: Int) {
        binding.tvAttempt.text = "Attempt $attempt of 3"
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressSave.visibility = if (state.isSaving) android.view.View.VISIBLE else android.view.View.GONE
                state.error?.let { Toast.makeText(this@ScratchCardActivity, it, Toast.LENGTH_LONG).show() }

                if (state.isSaved) {
                    val intent = Intent(this@ScratchCardActivity, SuccessActivity::class.java).apply {
                        putExtra(SuccessActivity.EXTRA_NAME, customerName)
                        putExtra(SuccessActivity.EXTRA_AGE, age)
                        putExtra(SuccessActivity.EXTRA_DISCOUNT, latestRevealedDiscount)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
