package com.example.doctorappoint.ui.service

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.ApiError
import com.example.doctorappoint.model.ScheduleData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookingViewModel : ViewModel(){

    private val _doctorSchedule = MutableStateFlow<NetworkResponse<List<List<ScheduleData>>>>(NetworkResponse.Loading)
    val doctorSchedule: StateFlow<NetworkResponse<List<List<ScheduleData>>>> = _doctorSchedule

    fun getDoctorSchedule(departmentId: Int, workingDate: String){
        Log.d("BookingViewModel", "Starting getDoctorSchedule - departmentId: $departmentId, workingDate: $workingDate")
        _doctorSchedule.value = NetworkResponse.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("BookingViewModel", "Making API call to fetch doctor schedule...")
                val response = RetrofitInstance.getDoctorSchedule().getDoctorSchedule(departmentId, workingDate)
                if(response.isSuccessful){
                    val scheduleResponse   = response.body()
                    if(scheduleResponse  != null && scheduleResponse.status == true ){
                        val scheduleDataList = scheduleResponse.data
                        Log.d("BookingViewModel", "API call successful, received ${scheduleDataList?.size} groups, ${scheduleDataList?.size} total schedules")
                        _doctorSchedule.value = NetworkResponse.Success(scheduleDataList)
                        Log.d("BookingViewModel", "StateFlow updated with success response.")
                    }else {
                        val message = scheduleResponse?.message ?: "Không có dữ liệu lịch khám cho ngày này."
                        _doctorSchedule.value = NetworkResponse.Error(message)
                        Log.e("BookingViewModel", "Successful API call (status true) but data is null: $message")
                    }
                }else{
                    val errorBody = response.errorBody()?.string()
                    var errorMessage: String
                    if(errorBody != null) {
                        try {
                            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                            errorMessage = apiError.message ?: "Lỗi không xác định từ server."
                            Log.e("BookingViewModel", "Error from API: $errorMessage (Code: ${response.code()})")
                        } catch (e: Exception) {
                            errorMessage = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                            Log.e("BookingViewModel", "Failed to parse error body: $errorBody", e)
                        }
                    }else{
                        errorMessage = "Lỗi server (mã: ${response.code()})."
                        Log.e("BookingViewModel", "Error response with empty body (Code: ${response.code()})")
                    }
                    _doctorSchedule.value = NetworkResponse.Error(errorMessage)
                    Log.d("BookingViewModel", "StateFlow updated with error response: $errorMessage")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                var errorMessage: String
                if (errorBody != null) {
                    try {
                        val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                        errorMessage = apiError.message ?: "Lỗi HTTP không xác định."
                        Log.e("BookingViewModel", "HTTP Exception: $errorMessage (Code: ${e.code()})", e)
                    } catch (parseError: Exception) {
                        errorMessage = "Lỗi HTTP không parse được body (mã: ${e.code()})."
                        Log.e("BookingViewModel", "HTTP Exception, but failed to parse error body: $errorBody", parseError)
                    }
                } else {
                    errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                    Log.e("BookingViewModel", "HTTP Exception with empty body (Code: ${e.code()})", e)
                }
                _doctorSchedule.value = NetworkResponse.Error(errorMessage)
                Log.d("BookingViewModel", "StateFlow updated with error response: $errorMessage")
            } catch (e: IOException) {
                val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
                Log.e("BookingViewModel", "Network Error: ${e.message}", e)
                _doctorSchedule.value = NetworkResponse.Error(errorMessage)
                Log.d("BookingViewModel", "StateFlow updated with error response: $errorMessage")
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Đã xảy ra lỗi không xác định."
                Log.e("BookingViewModel", "Unknown Error: $errorMessage", e)
                _doctorSchedule.value = NetworkResponse.Error(errorMessage)
                Log.d("BookingViewModel", "StateFlow updated with error response: $errorMessage")
            }
        }
    }


}