package com.example.doctorappoint.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.ApiError
import com.example.doctorappoint.model.HistoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class HistoryViewModel : ViewModel() {
    private val _historyState = MutableStateFlow<NetworkResponse<HistoryResponse>>(NetworkResponse.Loading)
    val historyState: StateFlow<NetworkResponse<HistoryResponse>> = _historyState

    fun getUserHistory(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            _historyState.value = NetworkResponse.Loading
            Log.d("HistoryViewModel", "Making API call to fetch user history...")
            try {
                val response = RetrofitInstance.getUserHistory().getUserHistory(token)

                if (response.isSuccessful) {
                    val historyResponse = response.body()
                    if (historyResponse != null && historyResponse.status) {
                        _historyState.value = NetworkResponse.Success(historyResponse)
                        Log.d("HistoryViewModel", "Get history success: $historyResponse")
                    } else {
                        val errorMessage = historyResponse?.message ?: "Lỗi tải lịch sử không xác định."
                        _historyState.value = NetworkResponse.Error(errorMessage)
                        Log.w("HistoryViewModel", "Get history failed with success status false: $errorMessage")
                    }
                } else {

                    val errorBody = response.errorBody()?.string()
                    var errorMessage: String
                    if (errorBody != null) {
                        try {
                            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                            errorMessage = apiError.message ?: "Lỗi từ server không xác định (mã: ${response.code()})."
                            Log.e("HistoryViewModel", "History API error: $errorMessage (Code: ${response.code()})")
                        } catch (e: Exception) {
                            errorMessage = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                            Log.e("HistoryViewModel", "Failed to parse error body: $errorBody", e)
                        }
                    } else {
                        errorMessage = "Lỗi server (mã: ${response.code()})."
                        Log.e("HistoryViewModel", "Error response with empty body (Code: ${response.code()})")
                    }
                    _historyState.value = NetworkResponse.Error(errorMessage)
                    Log.d("HistoryViewModel", "Get history failed with HTTP error: $errorMessage")
                }
            } catch (e: HttpException) {

                val errorBody = e.response()?.errorBody()?.string()
                var errorMessage: String
                if (errorBody != null) {
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                        errorMessage = apiError.message ?: "Lỗi HTTP không xác định (mã: ${e.code()})."
                        Log.e("HistoryViewModel", "History HTTP Exception: $errorMessage (Code: ${e.code()})", e)
                    } catch (parseError: Exception) {
                        errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                        Log.e("HistoryViewModel", "History HTTP Exception, failed to parse error body: $errorBody", parseError)
                    }
                } else {
                    errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                    Log.e("HistoryViewModel", "History HTTP Exception with empty body (Code: ${e.code()})", e)
                }
                _historyState.value = NetworkResponse.Error(errorMessage)
                Log.d("HistoryViewModel", "Get history failed with HTTP Exception: $errorMessage")
            } catch (e: IOException) {
                val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
                Log.e("HistoryViewModel", "History Network Error: ${e.message}", e)
                _historyState.value = NetworkResponse.Error(errorMessage)
                Log.d("HistoryViewModel", "Get history failed with Network Error: $errorMessage")
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định khi tải lịch sử."
                Log.e("HistoryViewModel", "History Unknown Error: $errorMessage", e)
                _historyState.value = NetworkResponse.Error(errorMessage)
                Log.d("HistoryViewModel", "Get history failed with Unknown Error: $errorMessage")
            }
        }
    }
}