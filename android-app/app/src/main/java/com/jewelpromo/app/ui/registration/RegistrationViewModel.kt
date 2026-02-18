package com.jewelpromo.app.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jewelpromo.app.data.repository.PromoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: Int? = null,
    val age: Int? = null,
    val chances: List<Int> = emptyList(),
)

class RegistrationViewModel(private val repository: PromoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun submitCustomer(name: String, mobile: String, dobIso: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.registerCustomer(name, mobile, dobIso)
            result.onSuccess { response ->
                _uiState.value = RegistrationUiState(
                    isLoading = false,
                    userId = response.userId,
                    age = response.age,
                    chances = response.chances,
                )
            }.onFailure { throwable ->
                _uiState.value = RegistrationUiState(
                    isLoading = false,
                    error = throwable.message ?: "Something went wrong during registration",
                )
            }
        }
    }
}

class RegistrationViewModelFactory(private val repository: PromoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
