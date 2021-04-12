package com.example.pruebakotlin.Persistencia.Entity

class Negocio(ID:Int,Direccion:String, NombreLocal:String,NombreDueño:String, Foto:Int,Latitud:Double,Longitud:Double) {

    var IdNegocio:Int=0
    var Direccion:String=""
    var NombreLocal:String=""
    var NombreDueño:String=""
    var Foto:Int=0
    var Latitud:Double=0.0
    var Longitud:Double=0.0


    init {
        this.NombreDueño=NombreDueño
        this.NombreLocal=NombreLocal
        this.Direccion=Direccion
        this.Foto=Foto
        this.IdNegocio=ID
        this.Latitud=Latitud
        this.Longitud=Longitud
    }

}