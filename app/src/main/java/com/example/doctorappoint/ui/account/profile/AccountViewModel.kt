package com.example.doctorappoint.ui.account.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AccountViewModel : ViewModel(){
    private val _logoutState = MutableStateFlow<NetworkResponse<Unit>>(NetworkResponse.Loading)
    val logoutState: StateFlow<NetworkResponse<Unit>> = _logoutState

    fun logout(token: String) {
        viewModelScope.launch {
            try {
                Log.d("LogoutViewModel", "Starting logout process ")
                _logoutState.value = NetworkResponse.Loading
                Log.d("LogoutViewModel", "Token: $token")
                val response = RetrofitInstance.getUserApi().userLogout(token)
                if (response.isSuccessful) {
                    _logoutState.value = NetworkResponse.Success(Unit)
                } else {
                    Log.d("Logout", "Response code: ${response.code()}, body: ${response.body()}, error: ${response.errorBody()?.string()}")
                    _logoutState.value = NetworkResponse.Error("Logout failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _logoutState.value =
                    NetworkResponse.Error("Logout error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }


}

