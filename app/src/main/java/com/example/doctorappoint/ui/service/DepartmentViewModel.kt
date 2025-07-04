package com.example.doctorappoint.ui.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.Department
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class DepartmentViewModel : ViewModel() {
    
    private val _departments = MutableStateFlow<NetworkResponse<List<Department>>>(NetworkResponse.Loading)
    val departments: StateFlow<NetworkResponse<List<Department>>> = _departments
    
    init {
        fetchDepartments()
    }
    
    private fun fetchDepartments() {
        viewModelScope.launch {
            try {
                _departments.value = NetworkResponse.Loading
                val departmentItems = RetrofitInstance.getDepartmentList().getDepartments()
                

                val uiDepartments = departmentItems.map { item ->
                    Department(
                        id = item.id,
                        name = item.name,
                        price = item.price,
                        description = null
                    )
                }
                
                _departments.value = NetworkResponse.Success(uiDepartments)
            } catch (e: Exception) {
                _departments.value = NetworkResponse.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun refreshDepartments() {
        fetchDepartments()
    }

    var selectedDepartment by mutableStateOf<Department?>(null)
        private set

    var selectedDate by mutableStateOf<LocalDate?>(null)
        private set

    fun selectDepartment(department: Department) {
        selectedDepartment = department
    }

    fun selectDate(date: LocalDate) {
        selectedDate = date
    }
}