package com.example.pruebakotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pruebakotlin.Ejemplos.MapaJava
import com.example.pruebakotlin.Utilitis.NetworkConecction
import com.example.pruebakotlin.Utilitis.myViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var TxtUsuario:TextInputEditText
    private lateinit var TxtPassword:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //INICIALIAR FIREBASE AUTH
        auth = Firebase.auth

        val BtnIngreso=findViewById<MaterialButton>(R.id.BtnIngresar);
        val BtnRegistrar=findViewById<MaterialButton>(R.id.BtnRegistrar);
        TxtUsuario=findViewById<TextInputEditText>(R.id.TxtUsuario)
        TxtPassword=findViewById<TextInputEditText>(R.id.TxtPassword)


        BtnIngreso.setOnClickListener(View.OnClickListener {
           setupLogin()
        })

        BtnRegistrar.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        })

      /*val networkConecction = NetworkConecction(applicationContext)
        networkConecction.observe(this, Observer { isConnect ->
            if (isConnect){
                ShowAlert("Conectado")
            }else{
                ShowAlert("Desconectado")

            }

        })*/

    }


    private fun setupLogin(){

        val email:String=TxtUsuario.text.toString().trim()
        val password:String=TxtPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("email", task.result?.user)

                        //GUARDAR DATOS DE USUARIO EN PREFERENCIAS
                        savePreferences(task.result?.user.toString())

                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun ShowAlert(mensaje: String){

        val alert = MaterialAlertDialogBuilder(this)
            .setTitle("Internet")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)

        alert.show()
    }

    private fun savePreferences(email:String){
        //GUARDAR DATOS DE USUARIO EN PREFERENCIAS
        val preferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  preferences.edit()
        editor.putString("user", email)
        editor.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}