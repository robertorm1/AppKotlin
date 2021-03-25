package com.example.pruebakotlin.Persistencia.Retrofit

import com.example.pruebakotlin.Persistencia.Entity.Negocio
import com.example.pruebakotlin.Persistencia.Entity.User
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface serviceRetrofit {

    @POST("getUser")
    @FormUrlEncoded
    fun getUserInfo(@Field("email") email:String): Call<JsonObject>

    @GET("insertUser")
    fun postUserInsert(@Body user: User):Call<JsonObject>

}