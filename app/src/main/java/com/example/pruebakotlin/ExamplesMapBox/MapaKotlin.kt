package com.example.pruebakotlin.ExamplesMapBox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.example.pruebakotlin.R
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager

class MapaKotlin : AppCompatActivity()  {

    private var mapView: MapView? = null
    private var markerViewManager: MarkerViewManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_mapa)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)


        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.LIGHT) {
            }

            markerViewManager = MarkerViewManager(mapView, mapboxMap)
            addMarker()

            val position = CameraPosition.Builder()
                .target(LatLng(21.1452616,-101.6917503))
                .zoom(mapboxMap.maxZoomLevel-8)
                //.bearing(50.0)
                //.tilt(30.0)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 4000)

            mapboxMap.addOnMapClickListener { point ->
                Toast.makeText(this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show()
                true
            }

        }

    }

    private fun addMarker(){
       markerViewManager?.let {
           val imageView = ImageView(this)
           imageView.setImageResource(R.mipmap.ic_marker)
           imageView.layoutParams = FrameLayout.LayoutParams(100, 100)
           val markerView = MarkerView(LatLng(21.1452616,-101.6917503), imageView)
           it.addMarker(markerView)
       }
    }

    /*override fun onMapReady(mapboxMap: MapboxMap) {

        mapboxMap.setStyle(Style.OUTDOORS) {
            // Use a layer manager here
        }
        mapboxMap.addOnMapClickListener { point ->
            Toast.makeText(this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show()
            true
        }

    }*/

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}
