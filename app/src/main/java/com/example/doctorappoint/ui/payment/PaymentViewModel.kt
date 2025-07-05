package com.example.doctorappoint.ui.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.CreateOrder
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
                val code = data.getString("return_code")

                if (code == "1") {
                    val zpTransToken = data.getString("zp_trans_token")
                    _uiState.value = _uiState.value.copy(
                        zpTransToken = zpTransToken,
                        showToken = true,
                        isLoading = false,
                        successMessage = "Tạo đơn hàng thành công"
                    )
                } else {
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
