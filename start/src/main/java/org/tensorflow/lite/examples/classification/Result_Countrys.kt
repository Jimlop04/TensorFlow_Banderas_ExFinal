package org.tensorflow.lite.examples.classification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import com.squareup.picasso.Picasso
import org.tensorflow.lite.examples.classification.Model.Country
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Result_Countrys : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_countrys)

        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val btnRegresar = findViewById<Button>(R.id.btnVolver)
        btnRegresar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                backActivity(view)
            }
        })
    }

    fun backActivity(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun getDataFromService(Codes: String): Call<Country> {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.geognos.com/api/en/countries/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val ServiceCountry = retrofit.create(service::class.java)
        val Service: Call<Country> = ServiceCountry.getCountrys(Codes)
        return  Service
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0;

        intent.getStringExtra("code")?.let {
            getDataFromService(it).enqueue(object : Callback<Country> {
                override fun onFailure(call: Call<Country>, t: Throwable) {
                    println("Se Presento un Error" + t.message)
                    Toast.makeText(applicationContext, "ERROR: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Country>, response: Response<Country>) {
                    val Result: Country = response.body()!!
                    addDataFromResponse(Result)

                    val defaultLat : LatLng = LatLng(Result.Results.GeoPt[0],Result.Results.GeoPt[1])

                    val north = Result.Results.GeoRectangle.North;
                    val west = Result.Results.GeoRectangle.West;
                    val east = Result.Results.GeoRectangle.East;
                    val south = Result.Results.GeoRectangle.South;

                    val rectagleOption : PolygonOptions = PolygonOptions()
                        .add(
                            LatLng(north, west),
                            LatLng(south, west),
                            LatLng(south, east),
                            LatLng(north, east),
                            LatLng(north, west)).strokeColor(Color.BLUE)
                    googleMap.addPolygon(rectagleOption)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLat, 4.0f))
                }
            })
        }
    }

    fun addDataFromResponse(countryResponse: Country){
        val capital = findViewById<TextView>(R.id.countryCapital)
        val codeIso2 = findViewById<TextView>(R.id.codeIso)
        val telPrefix = findViewById<TextView>(R.id.telPrefix)
        val rectangle = findViewById<TextView>(R.id.rectangles)
        val center = findViewById<TextView>(R.id.cordCenter)
        val img = findViewById<ImageView>(R.id.banderaImg)

        capital.setText(countryResponse.Results.Capital.Name)
        codeIso2.setText(countryResponse.Results.Codes.iso2)
        telPrefix.setText(countryResponse.Results.TelPref)
        rectangle.setText(countryResponse.Results.GeoRectangle.West.toString() + " "
                + countryResponse.Results.GeoRectangle.North.toString() + " "
                + countryResponse.Results.GeoRectangle.East.toString() + " "
                + countryResponse.Results.GeoRectangle.South.toString())
        center.setText(countryResponse.Results.GeoPt[0].toString() + " "
                + countryResponse.Results.GeoPt[1].toString())

        Picasso.get()
            .load("http://www.geognos.com/api/en/countries/flag/" + codeIso2.text.toString() + ".png")
            .into(img)
    }

}