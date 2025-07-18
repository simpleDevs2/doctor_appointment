package com.example.doctorappoint.ui.account.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.ApiResponse
import com.example.doctorappoint.model.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<NetworkResponse<LoginResponse>>(NetworkResponse.Loading)
    val loginState: StateFlow<NetworkResponse<LoginResponse>> = _loginState



    fun login(phone: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("LoginViewModel", "Starting login process for phone: $phone")
            _loginState.value = NetworkResponse.Loading
            try {
                val response = RetrofitInstance.getUserApi().userLogin(phone, password)
                if (response.isSuccessful) {
                   val loginResponse = response.body()
                    if(loginResponse != null && loginResponse.status){
                        _loginState.value = NetworkResponse.Success(loginResponse)
                    }
                    else{
                        val errorMessage = loginResponse?.message ?: "Lỗi đăng nhập không xác định."
                        _loginState.value = NetworkResponse.Error(errorMessage)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    var errorMessage: String
                    if(errorBody != null){
                        try{
                            val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                            errorMessage = apiError.message ?: "Lỗi từ server không xác định (mã: ${response.code()})."
                            Log.e("LoginViewModel", "Login API error: $errorMessage (Code: ${response.code()})")

                        }catch (e: Exception){
                            errorMessage = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                            Log.e("LoginViewModel", "Failed to parse error body for code ${response.code()}: $errorBody", e)

                        }
                    }else{
                        errorMessage = "Lỗi server (mã: ${response.code()})."
                    }
                    _loginState.value = NetworkResponse.Error(errorMessage)
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                var errorMessage: String
                if(errorBody != null){
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                        errorMessage = apiError.message?: "Lỗi HTTP không xác định (mã: ${e.code()})."
                        Log.e("LoginViewModel", "Login HTTP Exception: $errorMessage (Code: ${e.code()})", e)

                    }catch (parseError: Exception){
                        errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                        Log.e("LoginViewModel", "Login HTTP Exception, failed to parse error body: $errorBody", parseError)

                    }
                }else{
                    errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                    Log.e("LoginViewModel", "Login HTTP Exception with empty body (Code: ${e.code()})", e)
                }
                _loginState.value = NetworkResponse.Error(errorMessage)
            }catch (e: IOException){
                val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
                Log.e("LoginViewModel", "Login Network Error: ${e.message}", e)
                _loginState.value = NetworkResponse.Error(errorMessage)

            }catch (e: Exception){
                val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định khi đăng nhập."
                Log.e("LoginViewModel", "Login Unknown Error: $errorMessage", e)
                _loginState.value = NetworkResponse.Error(errorMessage)
            }
        }
    }



}