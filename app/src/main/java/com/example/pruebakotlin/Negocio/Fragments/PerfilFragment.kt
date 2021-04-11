package com.example.pruebakotlin.Negocio.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pruebakotlin.Persistencia.Retrofit.retrofitClass
import com.example.pruebakotlin.Persistencia.Retrofit.serviceRetrofit
import com.example.pruebakotlin.R
import com.example.pruebakotlin.databinding.FragmentBuscadorBinding
import com.example.pruebakotlin.databinding.FragmentPerfilBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PerfilFragment : Fragment() {

    //ViewBinding Fragment
    private var _binding: FragmentPerfilBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater,container,false)

        validate()

        binding.CerrarSesion.setOnClickListener{
            removePrefereces()
        }

        return binding.root
    }


    private fun getUser(){
        val api: serviceRetrofit = retrofitClass.getRestEngine().create(serviceRetrofit::class.java)
        val call: Call<JsonObject> = api.getUserInfo(getEmail())

        call.enqueue(object :Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful){
                    val json: JsonObject? = response.body()
                    val status:Boolean = json?.get("status")!!.asBoolean
                    if(status) {
                        val jsonArray: JsonArray = json.get("response")!!.asJsonArray
                        val jsonObject: JsonObject = jsonArray.get(0).asJsonObject
                        val id_usr = jsonObject.get("id_usuario").asInt
                        val name_usr = jsonObject.get("nombre_usuario").asString

                        binding.TxtUserEmail.text = getUser().toString()
                        binding.TxtUserName.text = name_usr

                        savePreferences(id_usr,name_usr)
                    }else{
                        Toast.makeText(context,"No se econtro información", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context,t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }

    //CONSULTAR DATOS DE PREFERENCIA
    private fun getEmail():String{
        val preferences = activity?.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val email: String? = preferences?.getString("user",null)
        return email.toString();
    }

    //VALIDACION DE INFORMACION GUARDADA
    private fun validate(){
        val preferences = activity?.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val name=preferences?.getString("name",null)
        if (name!=null) {
            binding.TxtUserEmail.text = preferences.getString("user", null)
            binding.TxtUserName.text = preferences.getString("name", null)
        }else{
            getUser()
        }
    }

    //GAURDAR DATOS DE USUARIO
    private fun savePreferences(id:Int,name:String){
        //GUARDAR DATOS DE USUARIO EN PREFERENCIAS
        val preferences = activity?.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  preferences!!.edit()
        editor.putInt("id", id)
        editor.putString("name",name)
        editor.apply()
    }

    //LIMPIAR PREFERENCES
    private fun removePrefereces(){
        val preferences = activity?.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences!!.edit()
        editor.clear()
        editor.apply()
        this.activity?.finish()
    }

}