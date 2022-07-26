package com.rakuseru.storyapp1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.rakuseru.storyapp1.data.UserRepository
import com.rakuseru.storyapp1.data.remote.RequestLogin
import com.rakuseru.storyapp1.data.remote.ResponseLogin
import com.rakuseru.storyapp1.utils.Result
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _resLogin = MutableLiveData<Result<ResponseLogin>>()
    val resLogin: LiveData<Result<ResponseLogin>> = _resLogin

    fun login(requestLogin: RequestLogin) = viewModelScope.launch {
        _resLogin.value = Result.Loading() // IsLoading

        try {
            val response = userRepository.login(requestLogin)
            _resLogin.value = Result.Success(response.body()!!) // IsSuccess
        } catch (e: Exception) {
            _resLogin.value = Result.Error(e.message.toString()) // IsError
            Log.e("MainViewModel", e.message.toString())
        }
    }

    fun saveUser(token: String) = viewModelScope.launch {
        userRepository.saveUser(token)
    }

    fun fetchUser(): LiveData<String> {
        return userRepository.getUser().asLiveData()
    }
}