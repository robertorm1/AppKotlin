package com.example.pruebakotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pruebakotlin.Persistencia.Entity.NegocioAdd
import com.example.pruebakotlin.Persistencia.Retrofit.retrofitClass
import com.example.pruebakotlin.Persistencia.Retrofit.serviceRetrofit
import com.example.pruebakotlin.databinding.ActivityLoginBinding
import com.example.pruebakotlin.databinding.ActivityPlaceBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.mapbox.android.gestures.AndroidGesturesManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.android.gestures.MoveGestureDetector.OnMoveGestureListener
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.exceptions.ServicesException
import com.mapbox.geojson.Point
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
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class PlaceActivity : AppCompatActivity(),OnMapReadyCallback{

    private val REQUEST_CODE = 5678

    private var mapView: MapView? = null
    private var hoveringMarker: ImageView? = null

    private lateinit var mapboxMap: MapboxMap

    //Coordenadas de ubicación actual
    private lateinit var  originLocation: LatLng

    private lateinit var progressDialog:ProgressDialog

    //ViewBinding
    private lateinit var binding: ActivityPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(binding.root)

        // Initialize the mapboxMap view
        mapView = findViewById(R.id.mapView)
        mapView?.getMapAsync(this)

        binding.topAppBarPlace.setNavigationOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.BtnOpenPlace.setOnClickListener {
            bottomDialog()
        }

        dialogInicio()
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.uiSettings.isRotateGesturesEnabled = false
        mapboxMap.uiSettings.isZoomGesturesEnabled=false

        mapboxMap.setStyle(Style.TRAFFIC_DAY) { style ->

            enabledLocation(style)
            hoveringMarker = ImageView(this)
            hoveringMarker!!.setImageResource(R.drawable.ic_pin)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            hoveringMarker!!.setLayoutParams(params)
            mapView!!.addView(hoveringMarker)

        }

    }

    fun mapMoveEvent(){
        mapboxMap.addOnMoveListener(object : MapboxMap.OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                binding.CargadorPlace.visibility=View.VISIBLE
                binding.TxtDescripcion.visibility=View.GONE
            }
            override fun onMove(detector: MoveGestureDetector) {
            }
            override fun onMoveEnd(detector: MoveGestureDetector) {
                val mapTargetLatLng = mapboxMap.cameraPosition.target
                reverseGeocode(
                    Point.fromLngLat(
                        mapTargetLatLng.longitude,
                        mapTargetLatLng.latitude
                    )
                )

            }
        })

    }


    //GEOCODIFICACION INVERSA
    private fun reverseGeocode(point: Point) {
        try {
            val client = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build()

            client.enqueueCall(object : Callback<GeocodingResponse?> {
                override fun onResponse(
                    call: Call<GeocodingResponse?>,
                    response: Response<GeocodingResponse?>
                ) {
                    if (response.body() != null) {
                        val results =
                            response.body()!!.features()
                        if (results.size > 0) {
                            val feature = results[0]

                            mapboxMap.getStyle { style ->

                                    binding.CargadorPlace.visibility=View.GONE
                                    binding.TxtDescripcion.visibility=View.VISIBLE
                                    binding.TxtDescripcion.text=feature.placeName()

                            }
                        } else {
                            binding.CargadorPlace.visibility=View.GONE
                            binding.TxtDescripcion.visibility=View.VISIBLE
                            binding.TxtDescripcion.text="Dirección no encontrada"
                        }
                    }
                }

                override fun onFailure(
                    call: Call<GeocodingResponse?>,
                    throwable: Throwable
                ) {
                    Timber.e("Geocoding Failure: %s", throwable.message)
                    binding.CargadorPlace.visibility=View.GONE
                    binding.TxtDescripcion.visibility=View.VISIBLE
                    binding.TxtDescripcion.text="Error al Obtener la drireccion"
                }
            })


        } catch (servicesException: ServicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString())
            servicesException.printStackTrace()
            binding.CargadorPlace.visibility=View.GONE
            binding.TxtDescripcion.visibility=View.VISIBLE
            binding.TxtDescripcion.text="Error al obtener la dirirección"
        }
    }

    private fun setCameraPosition(location: LatLng) {
        val position = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(mapboxMap.getMaxZoomLevel()-10)
            .build()
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000)
    }

    private fun ValidarPermiso(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    @SuppressLint("MissingPermission")
    private fun enabledLocation(loadedMapStyle: Style) {
        if (ValidarPermiso()) {
            val locationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .build()
            )

            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.COMPASS
            originLocation = LatLng(locationComponent.lastKnownLocation)
            setCameraPosition(originLocation)
            mapMoveEvent()
        }
        else{
            Toast.makeText(this, "Faltan permisos de Ubicación, cierre la aplicación y vuelva a iniciarla", Toast.LENGTH_LONG).show()
        }
    }
    private fun bottomDialog(){
        val dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.activity_negocio, null)
        dialog.setContentView(bottomSheet)
        val TxtDueño = bottomSheet.findViewById<TextView>(R.id.TxtDueño)
        val TxtDireccionNegocio = bottomSheet.findViewById<TextView>(R.id.TxtDireccionNegocio)
        val TxtNombreNegocio = bottomSheet.findViewById<TextView>(R.id.TxtNombreNegocio)
        val TxtReferenciaNegocio = bottomSheet.findViewById<TextView>(R.id.TxtReferenciaNegocio)
        val BtnAddNegocio = bottomSheet.findViewById<MaterialButton>(R.id.BtnAddNegocio)

        val coordenadas ="Lat:"+originLocation.latitude+" "+"Long:"+originLocation.longitude
        TxtReferenciaNegocio.text=coordenadas
        TxtDireccionNegocio.text=binding.TxtDescripcion.text

        dialog.show()

        BtnAddNegocio.setOnClickListener {

            if (TxtDueño.text.isNotEmpty() && TxtDireccionNegocio.text.isNotEmpty() && TxtNombreNegocio.text.isNotEmpty()) {

                val negocio = NegocioAdd(
                     TxtDireccionNegocio.text.toString(),
                    1,
                     getID(),
                    "",
                    0,
                     TxtNombreNegocio.text.toString(),
                     TxtDueño.text.toString(),
                     originLocation.latitude,
                     originLocation.longitude
                )
                dialog.dismiss()
                cargador()
                postNegocio(negocio)

            } else {
                Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun postNegocio(negocio:NegocioAdd){
        val api: serviceRetrofit = retrofitClass.getRestEngine().create(serviceRetrofit::class.java)
        val call: Call<JsonObject> = api.postNegocio(negocio)

        call.enqueue(object :Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"Guardado",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,response.message(),Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext,t.message.toString(),Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

        })
    }

    //CONSULTAR DATOS DE PREFERENCIA
    private fun getID():Int{
        val preferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val id: Int = preferences.getInt("id",0)
        return id
    }

    fun cargador(){
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Guardando información")
        progressDialog.setMessage("Por favor espere")
        progressDialog.setCancelable(false)
        progressDialog.show()

    }
    fun dialogInicio() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Instruciones")
            .setMessage("Mueve el mapa para ajustar la ubicación")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, which ->
                reverseGeocode(
                    Point.fromLngLat(
                        originLocation.longitude,
                        originLocation.latitude
                    )
                )
            }
            .show()
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

