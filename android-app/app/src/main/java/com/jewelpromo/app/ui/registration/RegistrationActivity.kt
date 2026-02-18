package com.jewelpromo.app.ui.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jewelpromo.app.data.api.NetworkModule
import com.jewelpromo.app.data.repository.PromoRepository
import com.jewelpromo.app.databinding.ActivityRegistrationBinding
import com.jewelpromo.app.ui.scratch.ScratchCardActivity
import kotlinx.coroutines.launch
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private var selectedDobIso: String? = null

    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(PromoRepository(NetworkModule.promoApiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickDob.setOnClickListener { showDobPicker() }
        binding.btnSubmit.setOnClickListener { onSubmitClicked() }

        observeUiState()
    }

    private fun showDobPicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDobIso = String.format("%04d-%02d-%02d", year, month + 1, day)
                binding.tvSelectedDob.text = "DOB: $selectedDobIso"
            },
            calendar.get(Calendar.YEAR) - 18,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    private fun onSubmitClicked() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val mobile = binding.etMobile.text?.toString()?.trim().orEmpty()
        val dob = selectedDobIso

        if (name.isBlank() || mobile.isBlank() || dob.isNullOrBlank()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.submitCustomer(name, mobile, dob)
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE
                state.error?.let { Toast.makeText(this@RegistrationActivity, it, Toast.LENGTH_LONG).show() }

                val userId = state.userId
                val age = state.age
                if (userId != null && age != null && state.chances.size == 3) {
                    val intent = Intent(this@RegistrationActivity, ScratchCardActivity::class.java).apply {
                        putExtra(ScratchCardActivity.EXTRA_USER_ID, userId)
                        putExtra(ScratchCardActivity.EXTRA_NAME, binding.etName.text?.toString()?.trim().orEmpty())
                        putExtra(ScratchCardActivity.EXTRA_AGE, age)
                        putIntegerArrayListExtra(ScratchCardActivity.EXTRA_CHANCES, ArrayList(state.chances))
                    }
                    startActivity(intent)
                }
            }
        }
    }
}
