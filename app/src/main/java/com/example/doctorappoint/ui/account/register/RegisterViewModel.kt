package com.example.doctorappoint.ui.account.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.ApiResponse
import com.example.doctorappoint.model.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel : ViewModel(){
    private val _registerState = MutableStateFlow<NetworkResponse<RegisterResponse>>(NetworkResponse.Loading)
    val registerState: StateFlow<NetworkResponse<RegisterResponse>> = _registerState

    private val _isPhoneNumberExistsState = MutableStateFlow<NetworkResponse<ApiResponse>>(NetworkResponse.Loading)
    val isPhoneNumberExistsState: StateFlow<NetworkResponse<ApiResponse>> = _isPhoneNumberExistsState



    fun register(phoneNumber: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("RegisterViewModel", "Registering with phone number: $phoneNumber")
            _registerState.value = NetworkResponse.Loading
            try {
                val response = RetrofitInstance.getUserApi().userRegister(phoneNumber, password)
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && registerResponse.status) {
                        _registerState.value = NetworkResponse.Success(registerResponse)
                    } else {
                        val errorMessage = registerResponse?.message ?: "Unknown error"
                        _registerState.value = NetworkResponse.Error(errorMessage)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    var errorMessage: String
                    if (errorBody != null) {
                        try {
                            val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                            errorMessage = apiError.message
                                ?: "Loi server khong xac dinh (ma ${response.code()})"

                        } catch (e: Exception) {
                            errorMessage =
                                "Loi khi xu ly phan hoi tu server (ma ${response.code()})"
                            Log.e("RegisterViewModel", "Error parsing error response: ${e.message}")
                        }
                    } else {
                        errorMessage = "Loi server khong xac dinh (ma ${response.code()})"
                    }
                    _registerState.value = NetworkResponse.Error(errorMessage)
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                var errorMessage: String
                if (errorBody != null) {
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                        errorMessage =
                            apiError.message ?: "Lỗi HTTP không xác định (mã: ${e.code()})."
                        Log.e(
                            "RegisterViewModel",
                            "Login HTTP Exception: $errorMessage (Code: ${e.code()})",
                            e
                        )

                    } catch (parseError: Exception) {
                        errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                        Log.e(
                            "RegisterViewModel",
                            "Login HTTP Exception, failed to parse error body: $errorBody",
                            parseError
                        )

                    }
                } else {
                    errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                    Log.e(
                        "RegisterViewModel",
                        "Login HTTP Exception with empty body (Code: ${e.code()})",
                        e
                    )
                }
                _registerState.value = NetworkResponse.Error(errorMessage)
            } catch (e: IOException) {
                val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
                Log.e("RegisterViewModel", "Login Network Error: ${e.message}", e)
                _registerState.value = NetworkResponse.Error(errorMessage)

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định khi đăng nhập."
                Log.e("RegisterViewModel", "Login Unknown Error: $errorMessage", e)
                _registerState.value = NetworkResponse.Error(errorMessage)
            }
        }
    }

    suspend fun isPhoneNumberExists(phoneNumber: String): Boolean  {
        Log.d("RegisterViewModel", "Starting check phone number : $phoneNumber")
        _isPhoneNumberExistsState.value = NetworkResponse.Loading
        try {
            val response = RetrofitInstance.getUserApi().isPhoneExist(phoneNumber)
            if (response.isSuccessful) {
                val registerResponse = response.body()
                if (registerResponse  != null && registerResponse.status ) {
                    _isPhoneNumberExistsState.value = NetworkResponse.Success(registerResponse )
                    Log.d("RegisterViewModel", "check phone number success: $registerResponse ")
                    return true
                } else {
                    val errorMessage = registerResponse ?.message ?: "check phone number không thành công."
                    _isPhoneNumberExistsState.value = NetworkResponse.Error(errorMessage)
                    Log.d("RegisterViewModel", "check phone failed with success status false: $errorMessage")
                    return false
                }
            } else {
                val errorBody = response.errorBody()?.string()
                var errorMessage: String
                if (errorBody != null) {
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                        errorMessage = apiError.message ?: "Lỗi từ server không xác định."
                        Log.e("RegisterViewModel", "Confirm Payment API error: $errorMessage (Code: ${response.code()})")
                    } catch (e: Exception) {
                        errorMessage = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                        Log.e("RegisterViewModel", "Failed to parse error body: $errorBody", e)
                    }
                } else {
                    errorMessage = "Lỗi server (mã: ${response.code()})."
                    Log.e("RegisterViewModel", "Error response with empty body (Code: ${response.code()})")
                }
                _isPhoneNumberExistsState.value = NetworkResponse.Error(errorMessage)
                Log.d("RegisterViewModel", "Confirm Payment failed with HTTP error: $errorMessage")
                return false
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            var errorMessage: String
            if (errorBody != null) {
                try {
                    val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                    errorMessage = apiError.message ?: "Lỗi HTTP không xác định."
                    Log.e("RegisterViewModel", "Confirm Payment HTTP Exception: $errorMessage (Code: ${e.code()})", e)
                } catch (parseError: Exception) {
                    errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                    Log.e("RegisterViewModel", "Confirm Payment HTTP Exception, failed to parse error body: $errorBody", parseError)
                }
            } else {
                errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                Log.e("RegisterViewModel", "Confirm Payment HTTP Exception with empty body (Code: ${e.code()})", e)
            }
            _isPhoneNumberExistsState.value = NetworkResponse.Error(errorMessage)
            Log.d("RegisterViewModel", "Confirm Payment failed with HTTP Exception: $errorMessage")
            return false
        } catch (e: IOException) {
            val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
            Log.e("RegisterViewModel", "Confirm Payment Network Error: ${e.message}", e)
            _isPhoneNumberExistsState.value = NetworkResponse.Error(errorMessage)
            Log.d("RegisterViewModel", "Confirm Payment failed with Network Error: $errorMessage")
            return false
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định khi xác nhận thanh toán."
            Log.e("RegisterViewModel", "Confirm Payment Unknown Error: $errorMessage", e)
            _isPhoneNumberExistsState.value = NetworkResponse.Error(errorMessage)
            Log.d("RegisterViewModel", "Confirm Payment failed with Unknown Error: $errorMessage")
            return false
        }
    }

}