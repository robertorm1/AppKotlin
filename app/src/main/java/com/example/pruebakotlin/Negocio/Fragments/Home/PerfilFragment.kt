package com.example.pruebakotlin.Negocio.Fragments.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pruebakotlin.R
import com.example.pruebakotlin.databinding.FragmentBuscadorBinding
import com.example.pruebakotlin.databinding.FragmentPerfilBinding


class PerfilFragment : Fragment() {

    //ViewBinding Fragment
    private var _binding: FragmentPerfilBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}