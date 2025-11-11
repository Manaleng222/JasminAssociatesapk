package com.example.jasminassociates.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jasminassociates.models.User
import com.example.jasminassociates.services.AuthService
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _rememberMe = MutableLiveData<Boolean>()
    val rememberMe: LiveData<Boolean> = _rememberMe

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _hasError = MutableLiveData<Boolean>()
    val hasError: LiveData<Boolean> = _hasError

    private val _loginResult = MutableLiveData<Result<User>?>()
    val loginResult: LiveData<Result<User>?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _email.value = ""
        _password.value = ""
        _rememberMe.value = false
        _errorMessage.value = ""
        _hasError.value = false
        _loginResult.value = null
        _isLoading.value = false
    }

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                showError("Please enter both email and password.")
                _loginResult.value = Result.failure(Exception("Empty credentials"))
                return@launch
            }

            _isLoading.value = true
            _email.value = email
            _password.value = password
            _rememberMe.value = rememberMe

            try {
                val user = authService.authenticate(email, password)
                if (user != null) {
                    // Store user session
                    if (rememberMe) {
                        // Save email only (for security)
                        saveCredentials(email)
                    } else {
                        // Clear saved credentials
                        clearSavedCredentials()
                    }

                    clearError()
                    _loginResult.value = Result.success(user)
                } else {
                    showError("Invalid email or password.")
                    _loginResult.value = Result.failure(Exception("Authentication failed"))
                }
            } catch (e: Exception) {
                showError("Login failed: ${e.message}")
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEmail(email: String) {
        _email.value = email
        clearError()
    }

    fun updatePassword(password: String) {
        _password.value = password
        clearError()
    }

    fun updateRememberMe(remember: Boolean) {
        _rememberMe.value = remember
    }

    private fun saveCredentials(email: String) {
        sharedPreferences.edit().putString("remembered_email", email).apply()
    }

    private fun clearSavedCredentials() {
        sharedPreferences.edit().remove("remembered_email").apply()
    }

    private fun showError(message: String) {
        _errorMessage.value = message
        _hasError.value = true
    }

    private fun clearError() {
        _errorMessage.value = ""
        _hasError.value = false
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }


}

// ViewModel Factory
