package com.example.doctorappoint.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.HistoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val _historyState = MutableStateFlow<NetworkResponse<HistoryResponse>>(NetworkResponse.Loading)
    val historyState: StateFlow<NetworkResponse<HistoryResponse>> = _historyState

    fun getUserHistory(token: String){
        try {
            viewModelScope.launch() {
                _historyState.value = NetworkResponse.Loading
                Log.d("HistoryViewModel", "Making API call to fetch user history...")
                val response = RetrofitInstance.getUserHistory().getUserHistory(token)
                if (response.status) {
                    _historyState.value = NetworkResponse.Success(response)
                }else{
                    Log.w("HistoryViewModel", "Get history failed: ${response.message}")
                    // Handle authentication failure
                    _historyState.value = NetworkResponse.Error(response.message)
                }
            }
        }catch (e: Exception){
            _historyState.value = NetworkResponse.Error(e.message.toString())

        }
    }
}