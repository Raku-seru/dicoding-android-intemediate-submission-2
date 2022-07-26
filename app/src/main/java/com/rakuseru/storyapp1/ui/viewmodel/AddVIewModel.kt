package com.rakuseru.storyapp1.ui.viewmodel

import androidx.lifecycle.*
import com.rakuseru.storyapp1.data.StoryRepository
import com.rakuseru.storyapp1.data.UserRepository
import com.rakuseru.storyapp1.data.remote.ResponseMsg
import com.rakuseru.storyapp1.utils.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _uploadResponse = MutableLiveData<Result<ResponseMsg>>()
    val uploadResponse: LiveData<Result<ResponseMsg>> = _uploadResponse

    fun upload(token: String,
               file: MultipartBody.Part,
               description: RequestBody,
               lat: RequestBody?,
               lon: RequestBody?) =
        viewModelScope.launch {
            _uploadResponse.value = Result.Loading()
            try {
                val response = storyRepository.uploadStory(token, file, description, lat, lon)
                _uploadResponse.value = Result.Success(response.body()!!)
            } catch (e: Exception) {
                _uploadResponse.value = Result.Error(e.message.toString())
            }
        }

    fun fetchUser(): LiveData<String> {
        return userRepository.getUser().asLiveData()
    }
}