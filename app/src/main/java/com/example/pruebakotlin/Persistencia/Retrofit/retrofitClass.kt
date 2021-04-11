package com.example.pruebakotlin.Persistencia.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


 class retrofitClass {

     companion object {

         private val urlBase:String="http://192.168.1.82:4000/"

         fun getRestEngine(): Retrofit {
             val retrofit = Retrofit.Builder()
                 .baseUrl(urlBase)
                 .addConverterFactory(GsonConverterFactory.create())
                 .client(getResquestHeader())
                 .build()

             return retrofit
         }

         fun getResquestHeader():OkHttpClient{
             val okHttpClient = OkHttpClient.Builder()
                 .connectTimeout(30,TimeUnit.SECONDS)
                 .writeTimeout(30,TimeUnit.SECONDS)
                 .readTimeout(40,TimeUnit.SECONDS)
                 .build()

             return okHttpClient
         }

     }

}