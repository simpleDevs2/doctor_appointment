package com.example.doctorappoint.ui.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.CreateOrder
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.AppointmentResponse
import com.example.doctorappoint.model.ConfirmPaymentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        return try {
            Log.d("PaymentViewModel", "Starting make payment process for user: $userId")
            _appointmentState.value = NetworkResponse.Loading
            val response = RetrofitInstance.makeAppointment().makeAppointment(userId, scheduleDetailId, appointmentTime)
            if (response.status) {
                _appointmentState.value = NetworkResponse.Success(response)
                Log.d("PaymentViewModel", "Appointment success: ${response}")
                true
            } else {
                _appointmentState.value = NetworkResponse.Error(response.message)
                Log.d("PaymentViewModel", "Appointment failed: ${response.message}")
                false
            }
        } catch (e: Exception) {
            Log.d("PaymentViewModel", "Appointment failed with exception", e)
            _appointmentState.value = NetworkResponse.Error(e.message.toString())
            false
        }
    }

    suspend fun confirmPayment(userId: Int, bookingId: Int, zpTransId: String): Boolean {
        return try {
            Log.d("PaymentViewModel", "Starting confirm payment process for user: $userId")
            _confirmPaymentState.value = NetworkResponse.Loading
            val response = RetrofitInstance.confirmPayment().confirmPayment(userId, bookingId, zpTransId)
            if (response.status) {
                _confirmPaymentState.value = NetworkResponse.Success(response)
                Log.d("PaymentViewModel", "Confirm payment success: ${response}")
                true
            } else {
                _confirmPaymentState.value = NetworkResponse.Error(response.message)
                Log.d("PaymentViewModel", "Confirm payment failed: ${response.message}")
                false
            }
        }catch (e: Exception){
            Log.d("PaymentViewModel", "confirm payment failed with exception", e)
            _confirmPaymentState.value = NetworkResponse.Error(e.message.toString())
            false
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
