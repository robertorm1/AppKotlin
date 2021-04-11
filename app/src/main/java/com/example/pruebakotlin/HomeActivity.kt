package com.example.pruebakotlin

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import butterknife.BindView
import com.example.pruebakotlin.Negocio.Fragments.BuscadorFragment
import com.example.pruebakotlin.Negocio.Fragments.HomeFragment
import com.example.pruebakotlin.Negocio.Fragments.PerfilFragment
import com.example.pruebakotlin.Negocio.Interfaces.IComunica
import com.example.pruebakotlin.databinding.ActivityLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mapbox.mapboxsdk.geometry.LatLng

class HomeActivity : AppCompatActivity(),IComunica {

    //ARREGLO DE PERMISOS PARA EL FUNCIONAMIENTO DEL HERE
    private val PERMISOS = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION,
        permission.INTERNET
    )

    var bandera = true

    //Coordenadas de ubicación actual
    private lateinit var  originLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.ativity_menu)

        val bottom_navigation=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val home_fragment=
            HomeFragment()
        val buscador_fragment=
            BuscadorFragment()
        val perfil_fragment=
            PerfilFragment()

        ValidarPermiso()

        makeCurrentFragment(home_fragment)

      bottom_navigation.setOnNavigationItemSelectedListener {
          when(it.itemId) {
              R.id.Home -> {
                  makeCurrentFragment(home_fragment)
              }
              R.id.Buscador -> {
                  //makeCurrentFragment(buscador_fragment)
                  //bottomDialog()
                  val intent=Intent(this,PlaceActivity::class.java)
                  startActivity(intent)
              }
              R.id.Perfil -> {
                  makeCurrentFragment(perfil_fragment)
              }
              else -> false
          }

          true
      }
    }

    private fun makeCurrentFragment(fragmet: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.contenedor_menu, fragmet)
            commit()
        }}


    private fun bottomDialog(){
        val dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.activity_negocio, null)
        dialog.setContentView(bottomSheet)
        dialog.show()

    }

    private fun ValidarPermiso(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, permission.INTERNET)==PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions(PERMISOS, 100)
        }
        return false
    }

    private fun Dialog_Permisos() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permisos Desactivados")
            .setMessage("Debe aceptar los permisos para el correcto funcionamiento de la Aplicación")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, which ->
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    requestPermissions(PERMISOS, 100)
                }
            }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permission.ACCESS_FINE_LOCATION
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permission.ACCESS_COARSE_LOCATION
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission.INTERNET)) {

                    if (bandera) {
                        Dialog_Permisos()
                        bandera = false
                    } else {
                        Toast.makeText(
                            this,
                            "Vuelva iniciar la app ya acepte todos los permisos",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    override fun location(latLng: LatLng) {
      originLocation=latLng
        Toast.makeText(applicationContext,latLng.toString(),Toast.LENGTH_SHORT).show()
    }
}


