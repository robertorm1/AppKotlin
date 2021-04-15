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

        getInfo()

        binding.CerrarSesion.setOnClickListener{
            removePrefereces()
        }

        return binding.root
    }

    //VALIDACIÓN DE INFORMACION GUARDADA
    private fun getInfo(){
        val preferences = activity?.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        binding.TxtUserEmail.text = preferences?.getString("user", null)
        binding.TxtUserName.text = preferences?.getString("name", null)
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