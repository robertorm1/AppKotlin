@file:Suppress("DEPRECATION")

package com.example.pruebakotlin.Utilitis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkConecction(private val context:Context):LiveData<Boolean>() {

    private var connectivityManager:ConnectivityManager=
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var networkCallback:ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()

        updateConnection()
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->{
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }else ->{
                context.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    override fun onInactive() {
        super.onInactive()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
        }else{
            context.unregisterReceiver(networkReceiver)
        }
    }

    private fun connectivityManagerCallback():ConnectivityManager.NetworkCallback{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            networkCallback = object :ConnectivityManager.NetworkCallback(){
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)
                }
            }
            return networkCallback

        }else{
            throw IllegalAccessError("ERR")
        }
    }

    private val networkReceiver= object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }


    private fun updateConnection(){
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}