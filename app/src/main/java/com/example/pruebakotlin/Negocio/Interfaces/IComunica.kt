package com.example.pruebakotlin.Negocio.Interfaces

import com.mapbox.mapboxsdk.geometry.LatLng

interface IComunica {

    fun location(latLng: LatLng)
}