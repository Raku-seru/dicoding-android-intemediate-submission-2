package com.rakuseru.storyapp1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakuseru.storyapp1.data.UserRepository
import com.rakuseru.storyapp1.data.remote.RequestRegister
import com.rakuseru.storyapp1.data.remote.ResponseMsg
import com.rakuseru.storyapp1.utils.Result
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private var _registerResponse = MutableLiveData<Result<ResponseMsg>>()
    val registerResponse: LiveData<Result<ResponseMsg>> = _registerResponse

    // Call API Call to Register
    fun register(requestRegister: RequestRegister) = viewModelScope.launch {
        _registerResponse.value = Result.Loading()
        try {
            val response = repository.register(requestRegister)
            _registerResponse.value = Result.Success(response.body()!!)
        } catch (e: Exception) {
            _registerResponse.value = Result.Error(e.message)
            Log.e("RegisterViewModel",e.message.toString())
        }
    }

}