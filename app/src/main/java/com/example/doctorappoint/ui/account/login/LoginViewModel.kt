package com.example.doctorappoint.ui.account.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<NetworkResponse<LoginResponse>>(NetworkResponse.Loading)
    val loginState: StateFlow<NetworkResponse<LoginResponse>> = _loginState



    fun login(phone: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting login process for phone: $phone")
                _loginState.value = NetworkResponse.Loading
                
                val response = RetrofitInstance.getUserApi().userLogin(phone, password)
                
                Log.d("LoginViewModel", "API Response received: $response")
                Log.d("LoginViewModel", "Response status: ${response.status}")
                Log.d("LoginViewModel", "Response message: ${response.message}")
                Log.d("LoginViewModel", "User data: ${response.user}")
                Log.d("LoginViewModel", "Token: ${response.token}")
                
                if (response.status) {
                    Log.d("LoginViewModel", "Login successful!")
                    _loginState.value = NetworkResponse.Success(response)
                } else {
                    Log.w("LoginViewModel", "Login failed: ${response.message}")
                    // Handle authentication failure
                    _loginState.value = NetworkResponse.Error("Số điện thoại hoặc mật khẩu không chính xác")
                }
                
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed with exception", e)
                Log.e("LoginViewModel", "Exception message: ${e.message}")
                Log.e("LoginViewModel", "Exception localized message: ${e.localizedMessage}")
                
                // Check if it's an authentication error 
                val errorMessage = when {
                    e.message?.contains("401") == true || 
                    e.message?.contains("403") == true ||
                    e.message?.contains("Unauthorized") == true ||
                    e.message?.contains("Forbidden") == true -> {
                        "Số điện thoại hoặc mật khẩu không chính xác"
                    }
                    e.message?.contains("404") == true -> {
                        "User not found"
                    }
                    e.message?.contains("500") == true -> {
                        "Server error. Please try again later"
                    }
                    else -> {
                        "Network error. Please check your connection"
                    }
                }
                
                _loginState.value = NetworkResponse.Error(errorMessage)
            }
        }
    }
    
    fun clearError() {
        _loginState.value = NetworkResponse.Loading
    }


}