package org.com.hcmurs.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.com.hcmurs.common.enum.LoadStatus
import org.com.hcmurs.repositories.Api
import org.com.hcmurs.repositories.MainLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val status: LoadStatus = LoadStatus.Init()
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val log: MainLog?, // For logging, similar to LoginViewModel
    private val api: Api?     // For making API calls
) : ViewModel() {
    val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun reset() {
        _uiState.value = _uiState.value.copy(status = LoadStatus.Init())
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val result = api?.register(uiState.value.username, uiState.value.email, uiState.value.password)

                _uiState.value = _uiState.value.copy(status = LoadStatus.Success())
                log?.d("RegisterViewModel", "Registration successful for user: ${uiState.value.username}")

            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(status = LoadStatus.Error(ex.message.toString()))
                log?.e("RegisterViewModel", "Registration failed: ${ex.message}")
            }
        }
    }
}