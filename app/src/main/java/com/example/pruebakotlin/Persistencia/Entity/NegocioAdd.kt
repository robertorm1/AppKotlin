package com.example.pruebakotlin.Persistencia.Entity

data class NegocioAdd(
    val direccion: String,
    val fk_referencia: Int,
    val fk_usuario: Int,
    val foto: String,
    val id_negocio: Int,
    val local: String,
    val nombre: String,
    val latitud: Double,
    val longitud: Double
)