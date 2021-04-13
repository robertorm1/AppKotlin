package com.example.pruebakotlin.Negocio.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebakotlin.Negocio.Adapters.NegocioAdapter
import com.example.pruebakotlin.Persistencia.Entity.Negocio
import com.example.pruebakotlin.Persistencia.Retrofit.retrofitClass
import com.example.pruebakotlin.Persistencia.Retrofit.serviceRetrofit
import com.example.pruebakotlin.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private var rootView:View?=null
    private var mapView: MapView? = null

    private lateinit var mapboxMap: MapboxMap
    private lateinit var markerViewManager: MarkerViewManager
    private lateinit var markerView: MarkerView

    //Lista de PV
    val negocio: ArrayList<Negocio> = ArrayList()
    var lst:RecyclerView? = null

    //Coordenadas de ubicación actual
    private lateinit var  originLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(activity as Activity, getString(R.string.mapbox_access_token))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        mapView?.onCreate(savedInstanceState)

        val BtnLayer =rootView!!.findViewById<FloatingActionButton>(R.id.BtnLayer);
        val BtnUbicacion = rootView!!.findViewById<FloatingActionButton>(R.id.BtnUbication);

        //REFERENCIA AL LAYOUT INCLUDE
        val view = rootView!!.findViewById<LinearLayout>(R.id.include_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(view)
        lst=view.findViewById<RecyclerView>(R.id.lst_negocios)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {

                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        mapView = rootView!!.findViewById(R.id.mapView1)
        mapView?.getMapAsync(this)

        BtnLayer.setOnClickListener {
            layerMap()
        }

        BtnUbicacion.setOnClickListener{
            setCameraPosition(originLocation)
        }

        return rootView
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.addOnMapClickListener(this)
        mapboxMap.uiSettings.isRotateGesturesEnabled = false

        mapboxMap.setStyle(Style.TRAFFIC_DAY) { style ->
            if(ValidarPermiso()) {
                enabledLocation(style)
            }
        }

    }

    override fun onMapClick(point: LatLng): Boolean {
        return false
    }

    private fun setCameraPosition(location: LatLng) {
        val position = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(mapboxMap.getMaxZoomLevel() - 8)
            //.bearing(50.0) //.tilt(30.0)
            .build()
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000)
    }

    private fun addMarker(location: LatLng, tag: Int) {
        markerViewManager = MarkerViewManager(mapView, mapboxMap)
        val imageView = ImageView(activity as Activity)
        imageView.tag=tag
        imageView.setImageResource(R.mipmap.ic_marker)
        val params = LinearLayout.LayoutParams(100, 100)
        imageView.layoutParams = params
        markerView = MarkerView(LatLng(location.latitude, location.longitude), imageView)
        imageView.setOnClickListener {
            val position:Int = imageView.tag as Int
            openDialog(position)
        }
        markerViewManager.addMarker(markerView)
    }

    private fun openDialog(position: Int) {
        val dialog = Dialog(activity as Activity)
        dialog.setContentView(R.layout.dialog_detail)
        val Local=dialog.findViewById<TextView>(R.id.TxtLocal)
        val Dueño=dialog.findViewById<TextView>(R.id.TxtDueño)
        val Direccion=dialog.findViewById<TextView>(R.id.TxtDireccionDetail)
        val Navigate = dialog.findViewById<Chip>(R.id.BtnNavigate)

        Navigate.setOnClickListener{
            try {
                dialog.dismiss()
                val coordinate: String =
                    negocio[position].Latitud.toString() + "," + negocio[position].Longitud.toString()
                val uri =
                    Uri.parse("geo:" + coordinate + "?z=16&q=" + coordinate + "(" + negocio[position].Direccion + ")")
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (ex: Exception) {
                println(ex.toString())
            }
        }

        Local.text= negocio[position].NombreLocal
        Dueño.text=negocio[position].NombreDueño
        Direccion.text=negocio[position].Direccion

        dialog.show()
    }

    private fun layerMap(){
        val dialog = Dialog(activity as Activity)
        dialog.setContentView(R.layout.dialog_tipo_mapa)
        dialog.setCancelable(false)
        val BtnMapaNormal = dialog.findViewById<ImageButton>(R.id.BtnMapaNormal)
        val BtnMapaHybrido = dialog.findViewById<ImageButton>(R.id.BtnMapaHybrido)
        val BtnMapaTerrain = dialog.findViewById<ImageButton>(R.id.BtnMapaTerrain)

        BtnMapaNormal.setOnClickListener {
            mapboxMap.setStyle(Style.TRAFFIC_DAY)
            dialog.dismiss()
        }

        BtnMapaHybrido.setOnClickListener {
            mapboxMap.setStyle(Style.SATELLITE)
            dialog.dismiss()
        }

        BtnMapaTerrain.setOnClickListener {
            mapboxMap.setStyle(Style.LIGHT)
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun ValidarPermiso(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    @SuppressLint("MissingPermission")
    private fun enabledLocation(loadedMapStyle: Style) {

            val locationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(activity as Activity, loadedMapStyle)
                    .build()
            )

            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.COMPASS
            originLocation = LatLng(locationComponent.lastKnownLocation)
            setCameraPosition(originLocation)
            getNegocio()

    }

    private fun getNegocio(){
        val api: serviceRetrofit = retrofitClass.getRestEngine().create(serviceRetrofit::class.java)
        val call: Call<JsonObject> = api.getNegocio()

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val json: JsonObject? = response.body()
                    val status: Boolean = json?.get("status")!!.asBoolean
                    if (status) {
                        val jsonArray: JsonArray = json.get("response")!!.asJsonArray
                        negocio.clear()

                        var position = 0
                        for (jsonElement: JsonElement in jsonArray) {
                            val jsonObject = jsonElement.asJsonObject

                            val id_negocio = jsonObject.get("id_negocio").asInt
                            val name_dueño = jsonObject.get("nombre_dueño").asString
                            val name_local = jsonObject.get("nombre_local").asString
                            val direccion = jsonObject.get("direccion_negocio").asString
                            val latitud = jsonObject.get("latitud").asDouble
                            val longitud = jsonObject.get("longitud").asDouble

                            negocio.add(
                                Negocio(
                                    id_negocio,
                                    direccion,
                                    name_local,
                                    name_dueño,
                                    position,
                                    latitud,
                                    longitud
                                )
                            )
                            addMarker(LatLng(latitud, longitud), position)
                            position++
                        }

                        val adapter =
                            NegocioAdapter(negocio)
                        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
                        lst?.addItemDecoration(
                            DividerItemDecoration(
                                activity,
                                LinearLayout.VERTICAL
                            )
                        )
                        lst?.layoutManager = layoutManager
                        lst?.adapter = adapter

                    } else {
                        Toast.makeText(context, "No se encontro información", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(activity, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}