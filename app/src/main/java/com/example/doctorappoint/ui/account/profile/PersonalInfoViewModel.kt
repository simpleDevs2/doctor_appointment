package com.example.doctorappoint.ui.account.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.UpdateProfile
import com.example.doctorappoint.model.UpdateProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel(){
    private val _updateProfileState = MutableStateFlow<NetworkResponse<UpdateProfileResponse>>(NetworkResponse.Loading)
    val updateProfileState: StateFlow<NetworkResponse<UpdateProfileResponse>> = _updateProfileState

    fun updateProfile(token: String, updateProfile: UpdateProfile) {
        viewModelScope.launch {
            _updateProfileState.value = NetworkResponse.Loading
            try {
                Log.d("PersonalInfoViewModel", "Updating profile with token: $token")
                Log.d("PersonalInfoViewModel", "Updating profile with updateProfile: $updateProfile")
                val response = RetrofitInstance.getUserApi().updateProfile(token, updateProfile)
                if (response.status) {
                   // LoginManager.saveLoginData(context, response.data, response.data.api_token)
                    _updateProfileState.value = NetworkResponse.Success(response)
                } else {
                    Log.d("PersonalInfoViewModel", "Update failed: ${response.message}")
                    _updateProfileState.value = NetworkResponse.Error(response.message)
                }
            } catch (e: Exception) {
                _updateProfileState.value = NetworkResponse.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}