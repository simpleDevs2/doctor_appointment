package com.example.doctorappoint.ui.account.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.data.api.RetrofitInstance
import com.example.doctorappoint.model.ApiResponse
import com.example.doctorappoint.model.UpdateProfile
import com.example.doctorappoint.model.UpdateProfileResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class PersonalInfoViewModel : ViewModel(){
    private val _updateProfileState = MutableStateFlow<NetworkResponse<UpdateProfileResponse>>(NetworkResponse.Loading)
    val updateProfileState: StateFlow<NetworkResponse<UpdateProfileResponse>> = _updateProfileState

    fun updateProfile(token: String, updateProfile: UpdateProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateProfileState.value = NetworkResponse.Loading
            Log.d("PersonalInfoViewModel", "Updating profile with token: $token")
            try {
                val response = RetrofitInstance.getUserApi().updateProfile(token, updateProfile)
                if (response.isSuccessful) {
                  val updateProfileResponse = response.body()
                    if(updateProfileResponse !=null && updateProfileResponse.status){
                        _updateProfileState.value = NetworkResponse.Success(updateProfileResponse)
                    }
                    else{
                        val errorMessage = updateProfileResponse?.message?: "Lỗi không xác định"
                        _updateProfileState.value = NetworkResponse.Error(errorMessage)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    var errorMessages : String
                    if(errorBody != null){
                        try{
                            val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                            errorMessages = apiError.message?:"Lỗi từ server không xác định (mã: ${response.code()})."
                            Log.e("PersonalInfoViewModel", "Update Profile API error: $errorMessages (Code: ${response.code()})")
                        }catch (e: Exception){
                            errorMessages = "Lỗi khi xử lý phản hồi lỗi từ server (mã: ${response.code()})."
                            Log.e("PersonalInfoViewModel", "Failed to parse error body for code ${response.code()}: $errorBody", e)
                        }
                    }
                    else{
                       errorMessages = "Lỗi server (mã: ${response.code()})."
                        Log.e("PersonalInfoViewModel", "Error response with empty body (Code: ${response.code()})")

                    }
                    _updateProfileState.value = NetworkResponse.Error(errorMessages)
                }
            }catch (e: HttpException){
                val errorBody = e.response()?.errorBody()?.string()
                var errorMessage : String
                if(errorBody != null){
                    try{
                        val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
                        errorMessage = apiError.message?:"Lỗi HTTP không xác định (mã: ${e.code()})."
                        Log.e("PersonalInfoViewModel", "Update Profile HTTP Exception: $errorMessage (Code: ${e.code()})",e)
                    }catch(parse : Exception){
                        errorMessage = "Lỗi HTTP nhưng không parse được body (mã: ${e.code()})."
                        Log.e("PersonalInfoViewModel", "Update Profile HTTP Exception, failed to parse error body: $errorBody", parse)

                    }
                }else{
                    errorMessage = "Lỗi HTTP (mã: ${e.code()})."
                    Log.e("PersonalInfoViewModel", "Update Profile HTTP Exception with empty body (Code: ${e.code()})", e)
                }
                _updateProfileState.value = NetworkResponse.Error(errorMessage)
            }
            catch (e: IOException){
                val errorMessage = "Không có kết nối internet hoặc lỗi mạng."
                Log.e("PersonalInfoViewModel", "Update Profile Network Error: ${e.message}", e)
                _updateProfileState.value = NetworkResponse.Error(errorMessage)
            }catch (e: Exception) {
                val errorMessage = e.message?: "Đã xảy ra lỗi không xác định khi cập nhật thông tin."
                Log.e("PersonalInfoViewModel", "Update Profile Unknown Error: $errorMessage", e)
                _updateProfileState.value = NetworkResponse.Error(errorMessage)

            }
        }
    }
}