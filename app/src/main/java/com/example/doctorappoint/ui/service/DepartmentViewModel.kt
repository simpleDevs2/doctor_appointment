package com.example.doctorappoint.ui.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.Department
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                        name = item.name,
                        price = "150.000đ", // Default price since API doesn't provide it
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
}