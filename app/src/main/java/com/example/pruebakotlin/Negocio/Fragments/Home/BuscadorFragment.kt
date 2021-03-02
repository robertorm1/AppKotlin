package com.example.pruebakotlin.Negocio.Fragments.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pruebakotlin.R


class BuscadorFragment : Fragment() {

    private var rootView:View?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView= inflater.inflate(R.layout.fragment_buscador, container, false)

        return rootView
    }

}