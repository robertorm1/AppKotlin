package com.example.pruebakotlin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var handler:Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler= Handler()
        handler.postDelayed({

            if (sesion()) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
            }

        },3000)
    }

    //CONSULTAR DATOS DE PREFERENCIA
    private fun sesion():Boolean{
        val preferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val email: String? =preferences.getString("user",null)

        if (email!=null){
            return true
        }
        return false;

    }
}