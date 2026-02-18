package com.jewelpromo.app.ui.scratch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jewelpromo.app.data.repository.PromoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScratchUiState(
    val attempt: Int = 1,
    val currentDiscount: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
)

class ScratchViewModel(
    private val repository: PromoRepository,
    private val chances: List<Int>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScratchUiState())
    val uiState: StateFlow<ScratchUiState> = _uiState.asStateFlow()

    fun revealCurrentChance(): Int {
        val index = (_uiState.value.attempt - 1).coerceIn(0, chances.lastIndex)
        val discount = chances[index]
        _uiState.value = _uiState.value.copy(currentDiscount = discount)
        return discount
    }

    fun riskNextChance() {
        if (_uiState.value.attempt < 3) {
            _uiState.value = _uiState.value.copy(
                attempt = _uiState.value.attempt + 1,
                currentDiscount = null,
                error = null,
            )
        }
    }

    fun lockDiscount(userId: Int, discount: Int) {
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val result = repository.updateDiscount(userId, discount)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = throwable.message ?: "Unable to save discount",
                )
            }
        }
    }
}

class ScratchViewModelFactory(
    private val repository: PromoRepository,
    private val chances: List<Int>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScratchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScratchViewModel(repository, chances) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
