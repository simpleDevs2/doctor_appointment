package com.example.doctorappoint.ui.account.register

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.services.OtpService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {
    private val otpService = OtpService()

    private val _otpState = MutableStateFlow<NetworkResponse<String>>(NetworkResponse.Loading)
    val otpState: StateFlow<NetworkResponse<String>> = _otpState

    private val _verificationState = MutableStateFlow<NetworkResponse<Boolean>>(NetworkResponse.Loading)
    val verificationState: StateFlow<NetworkResponse<Boolean>> = _verificationState

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    private var isOtpRequestInProgress = false

    fun setVerificationId(id: String) {
        _verificationId.value = id
    }

    fun sendOtp(phoneNumber: String, activity: Activity) {
        if (isOtpRequestInProgress) {
            return // Prevent multiple simultaneous requests
        }

        viewModelScope.launch {
            try {
                isOtpRequestInProgress = true
                _otpState.value = NetworkResponse.Loading

                otpService.sendOtp(
                    phoneNumber = phoneNumber,
                    activity = activity,
                    onCodeSent = { id ->
                        _verificationId.value = id
                        _otpState.value = NetworkResponse.Success("OTP sent successfully")
                        isOtpRequestInProgress = false
                    },
                    onVerificationFailed = { exception ->
                        _otpState.value = NetworkResponse.Error(exception.message ?: "Failed to send OTP")
                        isOtpRequestInProgress = false
                    }
                )
            } catch (e: Exception) {
                _otpState.value = NetworkResponse.Error(e.message ?: "Unknown error occurred")
                isOtpRequestInProgress = false
            }
        }
    }

    fun verifyOtp(otpCode: String) {
        viewModelScope.launch {
            try {
                _verificationState.value = NetworkResponse.Loading

                _verificationId.value?.let { id ->
                    val result = otpService.verifyOtp(id, otpCode)
                        .catch { exception ->
                            _verificationState.value = NetworkResponse.Error(exception.message ?: "Verification failed")
                        }
                        .first()

                    if (result) {
                        _verificationState.value = NetworkResponse.Success(true)
                    } else {
                        _verificationState.value = NetworkResponse.Error("Invalid OTP code")
                    }
                } ?: run {
                    _verificationState.value = NetworkResponse.Error("Verification ID not found")
                }
            } catch (e: Exception) {
                _verificationState.value = NetworkResponse.Error(e.message ?: "Verification failed")
            }
        }
    }

    fun resendOtp(phoneNumber: String, activity: Activity) {
        sendOtp(phoneNumber, activity)
    }

    fun clearStates() {
        _otpState.value = NetworkResponse.Loading
        _verificationState.value = NetworkResponse.Loading
        _verificationId.value = null
        isOtpRequestInProgress = false
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up Firebase auth when ViewModel is cleared
        otpService.signOut()
    }
}