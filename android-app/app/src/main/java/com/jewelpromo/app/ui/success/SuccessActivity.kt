package com.jewelpromo.app.ui.success

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jewelpromo.app.databinding.ActivitySuccessBinding
import com.jewelpromo.app.ui.registration.RegistrationActivity

class SuccessActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_DISCOUNT = "extra_discount"
    }

    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val age = intent.getIntExtra(EXTRA_AGE, 0)
        val discount = intent.getIntExtra(EXTRA_DISCOUNT, 0)

        binding.tvCongrats.text =
            "Congratulations $name! You have won a $discount% discount on making charges for your $age${ordinal(age)} birthday!"

        binding.btnNextCustomer.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun ordinal(number: Int): String {
        if (number % 100 in 11..13) return "th"
        return when (number % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}
