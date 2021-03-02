package com.example.pruebakotlin.ExamplesMapBox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.pruebakotlin.R
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val TxtNum1=findViewById<EditText>(R.id.TxtNum1)
        val TxtNum2=findViewById<EditText>(R.id.TxtNum2)
        val BtnCalcular=findViewById<MaterialButton>(R.id.BtnSuma);
        val LblResultado=findViewById<TextView>(R.id.LblResultado);

        operadores(TxtNum1.text.toString().toDouble(),TxtNum2.text.toString().toDouble(),1)

        BtnCalcular.setOnClickListener(View.OnClickListener{

        })
    }

    private fun variablesConstantes(){

        //Variables
        var variable:String=""
        variable="Hola Mundo desde variables"

        //Constantes
        val constante:String="Hola mundo desde contante"

        //Concatenacion
        println("$variable y $constante")

    }

    private fun setenciaIf(){

        val myNumber=10;

        if(myNumber>=10){
           println("El mayor")
        }else if (myNumber<20){
            println("El valor es menor a 20")
        }else if (myNumber==10){
            println("Los valores son iguales")
        }else{
            println("Error")
        }
    }

    private fun senteciaWhen(){

        //When de Strings
        val country="España"

        when(country){
            "España,Mexico,Colombia" ->{
                println("El idioma es español")
            }"USA" ->{
                println("El idioma es ingles")
            }"Francia" ->{
                println("El idioma es frances")
            }else ->{
                println("No encontrado")
            }
        }

        //When de Int
        val age=18

        when(age){
            0,1,2 ->{
                println("Eres un bebe")
            }in 3..10 ->{
                println("Eres un niño")
            }in 11..17 ->{
                println("Eres un adolecente")
            }else ->{
                println("Eres un adolecente")
            }
        }

    }

    private fun arrays(){

        //Creacion de array
        val myArray = arrayListOf<String>();
        myArray.add("Roberto")
        myArray.add("Omar")
        myArray.add("Luigui")

        //Agregar multiples registros a Array
        myArray.addAll(listOf("Jaqui","Teo"))

        //Modificacion de elemento de array
        myArray[2]="Ricardo"

        //Elimimar Elemento de array
        myArray.removeAt(3)

        //Tamaño de array
        myArray.count()

        //Primer elemento del array
        myArray.first()

        //Ultimo elemento del array
        myArray.last()

        println(myArray)

        //Recorrer array
        myArray.forEach{
            println(it)
        }

        //Limpiar array
        myArray.clear()
    }

    private fun maps(){

        //Declaracion de mapa
        var myMap:Map<String,Int> = mapOf();

        //Agregar datos a map
        myMap= mutableMapOf("Pedro" to 18,"Marcos" to 51)

        //Agregar un solo elemento al mapa
        myMap.put("Luigui",24)

        //Eliminar elemento de mapa
        myMap.remove("Pedro")

        println(myMap)
        println(myMap["Pedro"])
    }

    private fun bucles(){

        val myArray = arrayListOf<String>("Pedro","Omar","Ramiro")
        val myMap:Map<String,Int> = mutableMapOf("Brais" to 1,"Roberto" to 2)

        //For
        for (myString:String in myArray){
            println(myString)
        }
        for (myElement in myMap){
            println(myElement.key)
        }

        //Impmrime del 0 al 10
        for (x in 0..10){
            println(x)
        }

        //Da saltos de 2 en dos
        for (x in 0..10 step 2){
            println(x)
        }

        //Impmrime del 10 al 0
        for (x in 10 until  0){
            println(x)
        }

        //Imprime del 1 al 9
        for (x in 0 downTo 10){
            println(x)
        }

        //While
        var x= 0

        while (x<10){
            println(x)
            x+=1
        }
    }

    fun nullSafety() {

        var myString = "MoureDev"
        // myString = null Esto daría un error de compilación
        println(myString)

        // Variable null safety
        var mySafetyString: String? = "MoureDev null safety"
        mySafetyString = null
        println(mySafetyString)

        //mySafetyString = "Suscríbete!"
        //println(mySafetyString)

        /*if (mySafetyString != null) {
            println(mySafetyString!!)
        } else {
            println(mySafetyString)
        }*/

        // Safe call

        println(mySafetyString?.length)

        mySafetyString?.let {
            println(it)
        } ?: run {
            println(mySafetyString)
        }

    }

    //FUNCIONES
    private fun operadores(num1:Double,num2:Double,op:Int) : Double{

        val result= OperacionesJava()
            .operaciones(num1,num1,op)
        val resul1= OperacionesKotlin().operaciones(num1,num2,op)

        Toast.makeText(this,result.toString(),Toast.LENGTH_LONG).show()

        return result
    }

    private fun classes() {

        val brais = Programmer("Brais", 32, arrayOf(Programmer.Language.KOTLIN, Programmer.Language.SWIFT))
        println(brais.name)

        brais.age = 40
        brais.code()

        val sara = Programmer("Sara", 35, arrayOf(Programmer.Language.JAVA), arrayOf(brais))
        sara.code()

        println("${sara.friends?.first()?.name} es amigo de ${sara.name}")

    }

}