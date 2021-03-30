package com.example.pruebakotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.pruebakotlin.databinding.ActivityLoginBinding
import com.example.pruebakotlin.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //ViewBinding
    private lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //INICIALIAR FIREBASE AUTH
        auth = Firebase.auth

        binding.BtnIngresar.setOnClickListener(View.OnClickListener {
           setupLogin()
        })

        binding.BtnRegistrar.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        })

    }

    private fun observadorInternet(){
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

        val email:String=binding.TxtUsuario.text.toString().trim()
        val password:String=binding.TxtPassword.text.toString().trim()

        if(email.isNotEmpty() && password.isEmpty()){

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

        }else{
            Toast.makeText(this,"Favor de llenar los campos", Toast.LENGTH_SHORT).show()
        }
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