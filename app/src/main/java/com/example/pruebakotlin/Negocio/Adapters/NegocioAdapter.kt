package com.example.pruebakotlin.Negocio.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebakotlin.Persistencia.Entity.Negocio
import com.example.pruebakotlin.R

class NegocioAdapter (private val items:ArrayList<Negocio>):
    RecyclerView.Adapter<NegocioAdapter.NegocioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NegocioViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_lista,parent,false)
        return NegocioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NegocioViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class NegocioViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val ImageView=view.findViewById<ImageView>(R.id.Img_lista)
        private val TxtDireccion=view.findViewById<TextView>(R.id.TxtDireccion)
        private val TxtNombre=view.findViewById<TextView>(R.id.TxtNombre)

        fun bind (negocioItem: Negocio){
            //ImageView.setImageResource(negocioItem.Foto)
            TxtDireccion.text=negocioItem.Direccion
            TxtNombre.text=negocioItem.NombreLocal
        }

    }


}