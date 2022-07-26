package com.rakuseru.storyapp1.data.network

import com.rakuseru.storyapp1.data.remote.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun createUser(@Body requestRegister: RequestRegister): Response<ResponseMsg>

    @POST("login")
    suspend fun fetchUser(@Body requestLogin: RequestLogin): Response<ResponseLogin>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<ResponseStory>

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon : RequestBody?
    ): Response<ResponseMsg>
}