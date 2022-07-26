package com.rakuseru.storyapp1.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// for POST
data class RequestRegister(
    @SerializedName("name")
    var name: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String
)

// for POST
@Parcelize
data class RequestLogin(
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String
) : Parcelable

data class ResponseLogin(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)
data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class ResponseMsg(
    var error: Boolean,
    var message: String
)

data class ResponseStory(
    var error: String,
    var message: String,
    var listStory: List<ListStory>
)

@Parcelize
data class ListStory(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable
