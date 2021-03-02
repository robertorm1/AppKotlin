package com.example.pruebakotlin.Persistencia.Retrofit

import com.example.pruebakotlin.Persistencia.Entity.Negocio
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface serviceRetrofit {

    @POST("userInfo")
    @FormUrlEncoded
    fun getUserInfo(@Field("id") id:Int): Call<JsonObject>

    @GET("Negocios")
    fun getNegocio(@Body negocio: Negocio):Call<JsonObject>

}