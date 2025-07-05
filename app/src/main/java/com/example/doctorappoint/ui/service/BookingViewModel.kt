package com.example.doctorappoint.ui.service

import ScheduleResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel(){

    private val _doctorSchedule = MutableStateFlow<NetworkResponse<List<ScheduleResponse>>>(NetworkResponse.Loading)
    val doctorSchedule: StateFlow<NetworkResponse<List<ScheduleResponse>>> = _doctorSchedule

    fun getDoctorSchedule(departmentId: Int, workingDate: String){
        Log.d("BookingViewModel", "Starting getDoctorSchedule - departmentId: $departmentId, workingDate: $workingDate")
        _doctorSchedule.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                Log.d("BookingViewModel", "Making API call to fetch doctor schedule...")
                val response = RetrofitInstance.getDoctorSchedule().getDoctorSchedule(departmentId, workingDate)

                val flattenedResponse = response.flatten()
                
                Log.d("BookingViewModel", "API call successful, received ${response.size} groups, ${flattenedResponse.size} total schedules")
                _doctorSchedule.value = NetworkResponse.Success(flattenedResponse)
                Log.d("BookingViewModel", "StateFlow updated with success response: ")

            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error fetching doctor schedule", e)
                _doctorSchedule.value = NetworkResponse.Error(e.message ?: "An error occurred while fetching doctor schedule")
                Log.d("BookingViewModel", "StateFlow updated with error response: ${e.message}")
            }
        }
    }


}