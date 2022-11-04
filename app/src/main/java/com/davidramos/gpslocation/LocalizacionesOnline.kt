package com.davidramos.gpslocation

import android.content.Intent
import android.database.SQLException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import com.davidramos.gpslocation.databinding.ActivityLocalizacionesOnlineBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import kotlin.collections.ArrayList


class LocalizacionesOnline : AppCompatActivity(), AdapterView.OnItemSelectedListener{

    private lateinit var binding: ActivityLocalizacionesOnlineBinding

    private var connectSql = ConnectSql()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding = ActivityLocalizacionesOnlineBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnGPS.setOnClickListener { poscamion()  }

        binding.spinnerUnidad.onItemSelectedListener = this
viewUnidades()

    }






       fun viewUnidades(){

            try {

                val uni = resources.getStringArray(R.array.Unidades)


                val adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,uni)
                binding.spinnerUnidad.adapter = adaptador



            }catch (ex: SQLException){
                Toast.makeText(this,"Error.",Toast.LENGTH_SHORT).show()
            }
        }


      fun poscamion(){
                val numunidad = binding.spinnerUnidad.selectedItem.toString()

                val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("unidad", numunidad)

                startActivity(intent)



            }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {




    }



    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
