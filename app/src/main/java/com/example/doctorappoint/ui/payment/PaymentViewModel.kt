package com.example.doctorappoint.ui.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.CreateOrder
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.ApiError
import com.example.doctorappoint.model.AppointmentResponse
import com.example.doctorappoint.model.ConfirmPaymentResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class PaymentUiState(
    val amount: String = "10000",
    val zpTransToken: String = "",
    val isLoading: Boolean = false,
    val showToken: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed class PaymentEvent {
    data class AmountChanged(val amount: String) : PaymentEvent()
    object CreateOrder : PaymentEvent()
    object ProcessPayment : PaymentEvent()
    object ClearError : PaymentEvent()
    object ClearSuccess : PaymentEvent()
}

class PaymentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState

    private val _appointmentState = MutableStateFlow<NetworkResponse<AppointmentResponse>>(NetworkResponse.Loading)
    val appointmentState: StateFlow<NetworkResponse<AppointmentResponse>> = _appointmentState

    private val _confirmPaymentState = MutableStateFlow<NetworkResponse<ConfirmPaymentResponse>>(NetworkResponse.Loading)
    val confirmPaymentState: StateFlow<NetworkResponse<ConfirmPaymentResponse>> = _confirmPaymentState

    suspend fun makeAppointment(userId: Int, scheduleDetailId: Int, appointmentTime: String): Boolean {
        Log.d("PaymentViewModel", "Starting make appointment process for user: $userId")
        _appointmentState.value = NetworkResponse.Loading
        try {
            val response = RetrofitInstance.makeAppointment().makeAppointment(userId, scheduleDetailId, appointmentTime)
            if (response.isSuccessful) {

                val appointmentResponse = response.body()
                if (appointmentResponse != null && appointmentResponse.status) {
                    _appointmentState.value = NetworkResponse.Success(appointmentResponse)
                    return true
                } else {
                    val errorMessage = appointmentResponse?.message ?: "Lỗi đặt lịch không xác định."
                    _appointmentState.value = NetworkResponse.Error(errorMessage)
                    return false
                }
            } else {

                val errorBody = response.errorBody()?.string()
                var errorMessage: String
                if (errorBody != null) {
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                        errorMessage = apiError.message ?: "Lỗi từ server không xác định (mã: ${response.code()})."
                        Log.e("PaymentViewModel", "Appointment API error: $errorMessage (Code: ${response.code()})")
                    } catch (e: Exception) {
                        errorMessage = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                        Log.e("PaymentViewModel", "Failed to parse error body for code ${response.code()}: $errorBody", e)
                    }
                } else {
                    errorMessage = "Lỗi server (mã: ${response.code()})."
                    Log.e("PaymentViewModel", "Error response with empty body (Code: ${response.code()})")
                }
                _appointmentState.value = NetworkResponse.Error(errorMessage)
                return false
            }
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            var errorMessage: String
            if (errorBody != null) {
                try {
                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    errorMessage = apiError.message ?: "Lỗi HTTP không xác định (mã: ${e.code()})."
                    Log.e("PaymentViewModel", "Appointment HTTP Exception: $errorMessage (Code: ${e.code()})", e)
                } catch (parseError: Exception) {
                    errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                    Log.e("PaymentViewModel", "Appointment HTTP Exception, failed to parse error body: $errorBody", parseError)
                }
            } else {
                errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                Log.e("PaymentViewModel", "Appointment HTTP Exception with empty body (Code: ${e.code()})", e)
            }
            _appointmentState.value = NetworkResponse.Error(errorMessage)
            return false
        }
        catch (e: IOException){
            val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
            Log.e("PaymentViewModel", "Appointment Network Error: ${e.message}", e)
            _appointmentState.value = NetworkResponse.Error(errorMessage)
            return false
        }
        catch (e: Exception) {
            val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định khi đặt lịch."
            Log.e("PaymentViewModel", "Appointment Unknown Error: $errorMessage", e)
            _appointmentState.value = NetworkResponse.Error(errorMessage)
            return false
        }
    }

    suspend fun confirmPayment(userId: Int, bookingId: Int, zpTransId: String): Boolean {
        Log.d("PaymentViewModel", "Starting confirm payment process for user: $userId")
        _confirmPaymentState.value = NetworkResponse.Loading
        try {
            val response = RetrofitInstance.confirmPayment().confirmPayment(userId, bookingId, zpTransId)
            if (response.isSuccessful) {
                val confirmResponse = response.body()
                if (confirmResponse != null && confirmResponse.status) {
                    _confirmPaymentState.value = NetworkResponse.Success(confirmResponse)
                    Log.d("PaymentViewModel", "Confirm payment success: $confirmResponse")
                    return true
                } else {
                    val errorMessage = confirmResponse?.message ?: "Xác nhận thanh toán không thành công."
                    _confirmPaymentState.value = NetworkResponse.Error(errorMessage)
                    Log.d("PaymentViewModel", "Confirm payment failed with success status false: $errorMessage")
                    return false
                }
            } else {
                val errorBody = response.errorBody()?.string()
                var errorMessage: String
                if (errorBody != null) {
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                        errorMessage = apiError.message ?: "Lỗi từ server không xác định."
                        Log.e("PaymentViewModel", "Confirm Payment API error: $errorMessage (Code: ${response.code()})")
                    } catch (e: Exception) {
                        errorMessage = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                        Log.e("PaymentViewModel", "Failed to parse error body: $errorBody", e)
                    }
                } else {
                    errorMessage = "Lỗi server (mã: ${response.code()})."
                    Log.e("PaymentViewModel", "Error response with empty body (Code: ${response.code()})")
                }
                _confirmPaymentState.value = NetworkResponse.Error(errorMessage)
                Log.d("PaymentViewModel", "Confirm Payment failed with HTTP error: $errorMessage")
                return false
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            var errorMessage: String
            if (errorBody != null) {
                try {
                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    errorMessage = apiError.message ?: "Lỗi HTTP không xác định."
                    Log.e("PaymentViewModel", "Confirm Payment HTTP Exception: $errorMessage (Code: ${e.code()})", e)
                } catch (parseError: Exception) {
                    errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                    Log.e("PaymentViewModel", "Confirm Payment HTTP Exception, failed to parse error body: $errorBody", parseError)
                }
            } else {
                errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                Log.e("PaymentViewModel", "Confirm Payment HTTP Exception with empty body (Code: ${e.code()})", e)
            }
            _confirmPaymentState.value = NetworkResponse.Error(errorMessage)
            Log.d("PaymentViewModel", "Confirm Payment failed with HTTP Exception: $errorMessage")
            return false
        } catch (e: IOException) {
            val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
            Log.e("PaymentViewModel", "Confirm Payment Network Error: ${e.message}", e)
            _confirmPaymentState.value = NetworkResponse.Error(errorMessage)
            Log.d("PaymentViewModel", "Confirm Payment failed with Network Error: $errorMessage")
            return false
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định khi xác nhận thanh toán."
            Log.e("PaymentViewModel", "Confirm Payment Unknown Error: $errorMessage", e)
            _confirmPaymentState.value = NetworkResponse.Error(errorMessage)
            Log.d("PaymentViewModel", "Confirm Payment failed with Unknown Error: $errorMessage")
            return false
        }
    }



    private val orderApi = CreateOrder()

    fun handleEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.AmountChanged -> {
                _uiState.value = _uiState.value.copy(
                    amount = event.amount,
                    errorMessage = null
                )
            }

            PaymentEvent.CreateOrder -> {
                createOrder()
            }

            PaymentEvent.ProcessPayment -> {
                // Handle in Composable or Activity with WebView (if needed)
            }

            PaymentEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }

            PaymentEvent.ClearSuccess -> {
                _uiState.value = _uiState.value.copy(successMessage = null)
            }
        }
    }

    private fun createOrder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val data = orderApi.createOrder(_uiState.value.amount)
                Log.d("PaymentViewModel", "Amount: ${_uiState.value.amount}")
                Log.d("PaymentViewModel", "Amount: ${data.toString()}")
                val code = data.getString("return_code")
                if (code == "1") {
                    val zpTransToken = data.getString("zp_trans_token")
                    Log.d("PaymentViewModel", "Order creation : ${zpTransToken.toString()}")
                    _uiState.value = _uiState.value.copy(
                        zpTransToken = zpTransToken,
                        showToken = true,
                        isLoading = false,
                        successMessage = "Tạo đơn hàng thành công"
                    )
                } else {
                    Log.d("PaymentViewModel", "Order creation failed: ${data.toString()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Tạo đơn hàng thất bại: $code"
                    )
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Error creating order", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi: ${e.message}"
                )
            }
        }
    }

    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(zpTransToken = token)
    }
    fun clearToken() {
        _uiState.value = _uiState.value.copy(zpTransToken = "", showToken = false)
    }
}
