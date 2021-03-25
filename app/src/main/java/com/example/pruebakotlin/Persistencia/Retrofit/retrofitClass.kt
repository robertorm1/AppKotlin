package com.example.pruebakotlin.Persistencia.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


 class retrofitClass {

    private lateinit var miInstance:retrofitClass
    private val urlBase:String="https://api.github.com"

     var apiPost: serviceRetrofit? = null

     init {
         buildRetrofit();
     }

     @Synchronized
     fun getIntanciaRetrofit(): retrofitClass {
         if (miInstance == null) {
             miInstance = retrofitClass()
         }
         return miInstance
     }


     fun getResquestHeader():OkHttpClient{
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .readTimeout(40,TimeUnit.SECONDS)
            .build()

        return okHttpClient
    }

    fun buildRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getResquestHeader())
            .build()
        this.apiPost=retrofit.create(serviceRetrofit::class.java)
    }

    fun getService(): serviceRetrofit? {
        return this.apiPost
    }

}