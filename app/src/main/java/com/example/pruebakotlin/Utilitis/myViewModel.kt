package com.example.pruebakotlin.Utilitis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class myViewModel(application: Application) : AndroidViewModel(application) {
    val connectivity: LiveData<Boolean>
    init {
        connectivity = conectivityLiveData(application)
    }
}