package com.davidramos.gpslocation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.SQLException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.widget.ZoomButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.davidramos.gpslocation.databinding.ActivityLocalizacionesOnlineBinding
import com.davidramos.gpslocation.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.sql.PreparedStatement


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var map:GoogleMap
    private lateinit var binding: ActivityMainBinding
    private var connectSql = ConnectSql()





    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createFragment()
        binding.btnVolver.setOnClickListener { onBackPressed()
        finishActivity(1000)}





    }
    private fun createFragment(){

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {




    map = googleMap
        map.setOnMyLocationButtonClickListener (this)

        enableLocation()

        val myHandler = Handler(Looper.getMainLooper())
        val extras = intent.extras
        val unidad = extras?.getString("unidad")


        myHandler.post(object : Runnable {

            override fun run() {


        posUnidades(unidad?:"")


                myHandler.postDelayed(this, 5000 /*20 segundos*/)
            }

        })

    }


     fun posUnidades(unidad:String){


        try {

            val posunidades: PreparedStatement = connectSql.dbConn()?.prepareStatement("select top (1) dbo.REGISTRO.LATITUD,dbo.REGISTRO.LONGITUD,dbo.VEHICULO.ID_VEHICULO ,dbo.VEHICULO.COD_VEHICULO, dbo.REGISTRO.FECHA_HORA from dbo.REGISTRO  \n" +
                    "INNER JOIN dbo.VEHICULO\n" +
                    "ON dbo.REGISTRO.ID_VEHICULO = dbo.VEHICULO.ID_VEHICULO\n" +
                    "where COD_VEHICULO = '"+unidad+"'\n"+
                    "order by FECHA_HORA desc")!!
            val result = posunidades.executeQuery()
            val lat = ArrayList<Double>()
            val lon = ArrayList<Double>()

            if (result.next()){
                val LATITUD = result.getString("LATITUD")
                val LONGITUD = result.getString("LONGITUD")
                val LATITUD2 = LATITUD.filter { it in "0123456789-." }
                val LONGITUD2 = LONGITUD.filter { it in "0123456789-." }
                val LATITUD3 = LATITUD2.substring(0,LATITUD2.length -1)
                val LONGITUD3 = LONGITUD2.substring(0,LONGITUD2.length -1)
                //CONVERTIR COORDENADAS
                //LATITUD
                val delim ="."
                val  list  = LATITUD3.split(delim)  //  (17, 59, 72019)
                val valor1= "."+list.get(1).toDouble().div(60.toDouble())
                val valor2 = valor1.toString()
                val LATITUD4 = valor2.substring(3,valor2.length -14)
                val LATITUD5:String = list.get(0)+"."+LATITUD4+list.get(2)
               val LATITUD6 = LATITUD5.toDouble()
                //Longitud

                val  list2  = LONGITUD3.split(delim)
                val valorl= "."+list2.get(1).toDouble().div(60.toDouble())
                val valorl2 = valorl.toString()
                val LONGITUD4 = valorl2.substring(3,valorl2.length -14)
                val LONGITUD5:String = "-"+list2.get(0)+"."+LONGITUD4+list2.get(2)
                val LONGITUD6 =  LONGITUD5.toDouble()


                lat.add(LATITUD6)
                lon.add(LONGITUD6)
                val posbus = LatLng(lat.get(0), lon.get(0))
                val markerbus = map.addMarker(
                    MarkerOptions()
                        .position(posbus)
                        .title(unidad) // el error era el icono
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_m_autobus_foreground))

                )
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(posbus,19f),
                    4000,
                    null
                )



                Toast.makeText(this,"Posiciòn de la unidad: "+lat.get(0).toString()+" "+lon.get(0).toString(),Toast.LENGTH_LONG).show()




            }




        }catch (ex: SQLException){
            Toast.makeText(this,"Error.",Toast.LENGTH_SHORT).show()
        }
    }


    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()){
            //si
            map.isMyLocationEnabled = true

        }else{
            //no
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
    }else{
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE_LOCATION)
    }
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       when(requestCode){
           REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
               map.isMyLocationEnabled = true
           }else{
               Toast.makeText(this,"Para activar la localizacion ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
           }
           else -> {}
       }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this,"Para activar la localizacion ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {

        Toast.makeText(this,"Buscando tu localización", Toast.LENGTH_SHORT).show()
       return false



    }
}