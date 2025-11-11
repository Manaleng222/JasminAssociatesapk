package com.example.jasminassociates.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jasminassociates.services.AuthService

class LoginViewModelFactory(
    private val authService: AuthService,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authService, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}