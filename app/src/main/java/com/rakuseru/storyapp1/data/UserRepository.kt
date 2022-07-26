package com.rakuseru.storyapp1.data

import com.rakuseru.storyapp1.data.network.ApiService
import com.rakuseru.storyapp1.data.preference.UserPreference
import com.rakuseru.storyapp1.data.remote.RequestLogin
import com.rakuseru.storyapp1.data.remote.RequestRegister

class UserRepository (
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun login(login: RequestLogin) = apiService.fetchUser(login)
    suspend fun register(register: RequestRegister) = apiService.createUser(register)

    suspend fun saveUser(token: String) = userPreference.saveUser(token)
    fun getUser() = userPreference.getUser()
    suspend fun deleteUser() = userPreference.deleteUser()


    companion object {
        private var INSTANCE: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository {
            return INSTANCE ?: synchronized(this) {
                UserRepository(apiService, userPreference).also {
                    INSTANCE = it
                }
            }
        }
    }

}