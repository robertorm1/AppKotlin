package com.example.pruebakotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebakotlin.Persistencia.Entity.User
import com.example.pruebakotlin.Persistencia.Retrofit.retrofitClass
import com.example.pruebakotlin.Persistencia.Retrofit.serviceRetrofit
import com.example.pruebakotlin.databinding.ActivityLoginBinding
import com.example.pruebakotlin.databinding.ActivityRegistroBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //ViewBinding
    private lateinit var binding: ActivityRegistroBinding
    //Codigo de validación activityResult
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //INICIALIAR FIREBASE AUTH
        auth = Firebase.auth

        binding.TxtIniciaSesion.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

        binding.BtnRegistroUser.setOnClickListener(View.OnClickListener {
            setupRegistro()
        })

        binding.BtnGoogle.setOnClickListener(View.OnClickListener {

            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)

        })

        val analytics= FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("mesage", "Integracion completa")
        analytics.logEvent("InitScreen", bundle)
    }

    private fun setupRegistro(){

        val usr:String=binding.TxtNombreUsuario.text.toString()
        val email:String=binding.TxtEmail.text.toString().trim()
        val pass:String=binding.TxtRegistroPassword.text.toString().trim()

        if (usr.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()){

            if(validarFormulario()){
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Usuario registrado", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                            //Cerrar sesion de usuario
                            //Firebase.auth.signOut()
                        } else {
                            ShowAlert(task.exception.toString())
                        }
                    }
            }

        }else{
            ShowAlert("Favor de llenar todos los campos")
        }
    }

    private fun validarFormulario():Boolean{

        val country:Boolean=true

        when(country){
            !validarEmail(binding.TxtEmail.text.toString().trim()) -> {
                binding.TxtErrEmail.error = "*El correo no tiene el formarto correcto"
                return false
            }
            !validarPass(binding.TxtRegistroPassword.text.toString().trim()) -> {
                binding.TxtErrPassword.error = "*La contraseña debe ser mayor a 6 caracteres"
                return false
            }
            else ->{
                binding.TxtErrEmail.error=null
                binding.TxtErrPassword.error=null
                return true
            }
        }
    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun validarPass(pass: String):Boolean{
        if (binding.TxtRegistroPassword.length() >= 6 ){
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential= GoogleAuthProvider.getCredential(account.idToken,null)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this,HomeActivity::class.java)
                            startActivity(intent)
                            savePreferences(account.givenName.toString())
                        }else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: ApiException) {
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
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

    private fun saveUser(){
        val api: serviceRetrofit? = retrofitClass().getIntanciaRetrofit().getService()
        val user: User = User(binding.TxtEmail.text.toString(),binding.TxtNombreUsuario.text.toString(),true)
        val call: Call<JsonObject> = api!!.postUserInsert(user)
        call.enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    Toast.makeText(applicationContext,"Correcto",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext,t.message.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}