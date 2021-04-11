package com.example.pruebakotlin

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
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
    private var mapboxMap: MapboxMap? = null

    private var TxtDescripcion:TextView?=null
    private lateinit var Cargador:ProgressBar
    private lateinit var topAppBar:MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_place)

        // Initialize the mapboxMap view
        mapView = findViewById(R.id.mapView)
        mapView?.getMapAsync(this)
        TxtDescripcion=findViewById(R.id.TxtDescripcion)
        Cargador=findViewById(R.id.CargadorDireccion)
        topAppBar=findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener {
            finish()
        }

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.uiSettings.isRotateGesturesEnabled = false

        mapboxMap.setStyle(Style.TRAFFIC_DAY) { style ->
            hoveringMarker = ImageView(this)
            hoveringMarker!!.setImageResource(R.drawable.mapbox_ic_place)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            hoveringMarker!!.setLayoutParams(params)
            mapView!!.addView(hoveringMarker)

            mapMoveEvent()

        }
    }

    fun mapMoveEvent(){
        mapboxMap!!.addOnMoveListener(object : MapboxMap.OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                Cargador.visibility=View.VISIBLE
                TxtDescripcion!!.visibility=View.GONE
            }
            override fun onMove(detector: MoveGestureDetector) {
            }
            override fun onMoveEnd(detector: MoveGestureDetector) {
                val mapTargetLatLng = mapboxMap!!.cameraPosition.target
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

                            mapboxMap!!.getStyle { style ->

                                    Cargador.visibility=View.VISIBLE
                                    TxtDescripcion!!.visibility=View.GONE
                                    TxtDescripcion!!.setText(feature.placeName())

                            }
                        } else {
                            Cargador.visibility=View.GONE
                            TxtDescripcion!!.visibility=View.VISIBLE
                            TxtDescripcion!!.setText("Dirección no encontrada")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<GeocodingResponse?>,
                    throwable: Throwable
                ) {
                    Timber.e("Geocoding Failure: %s", throwable.message)
                    Cargador.visibility=View.GONE
                    TxtDescripcion!!.visibility=View.VISIBLE
                    TxtDescripcion!!.setText("Error al Obtener la drireccion")
                }
            })


        } catch (servicesException: ServicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString())
            servicesException.printStackTrace()
            Cargador.visibility=View.GONE
            TxtDescripcion!!.visibility=View.VISIBLE
            TxtDescripcion!!.setText("Error al Obtener la drireccion")
        }
    }

    private fun setCameraPosition(location: LatLng) {
        val position = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(mapboxMap!!.getMaxZoomLevel() - 20)
            .build()
        mapboxMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000)
    }



}

