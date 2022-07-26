package com.rakuseru.storyapp1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakuseru.storyapp1.data.StoryRepository
import com.rakuseru.storyapp1.data.remote.ResponseStory
import kotlinx.coroutines.launch
import com.rakuseru.storyapp1.utils.Result

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyResponse = MutableLiveData<Result<ResponseStory>>()
    val storyResponse: LiveData<Result<ResponseStory>> = _storyResponse

    fun getAllStoryWithLocation(token: String) = viewModelScope.launch {
        _storyResponse.value = Result.Loading()
        try {
            val response = storyRepository.getAllStoryWithLocation(token)
            if (response.isSuccessful) {
                _storyResponse.value = Result.Success(response.body()!!)
            }
        } catch (e: Exception) {
            _storyResponse.value = Result.Error(e.message)
        }
    }
}