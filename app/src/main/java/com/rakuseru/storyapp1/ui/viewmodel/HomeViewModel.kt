package com.rakuseru.storyapp1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rakuseru.storyapp1.data.StoryRepository
import com.rakuseru.storyapp1.data.UserRepository
import com.rakuseru.storyapp1.data.local.StoryEntity
import com.rakuseru.storyapp1.data.remote.ListStory
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
): ViewModel() {

    fun getStories(token: String): LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStory(token).cachedIn(viewModelScope)

    fun deleteUser() = viewModelScope.launch {
        userRepository.deleteUser()
    }

}