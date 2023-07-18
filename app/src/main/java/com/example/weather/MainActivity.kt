package com.example.weather

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {

    lateinit var searchView : SearchView
    lateinit var stringSv: String  // string entered in search view


    val wList = mutableListOf<Weather>()


    // google play api for retrieving current loc = https://developer.android.com/training/location/retrieve-current

    private lateinit var fusedLocationClient: FusedLocationProviderClient // instance created

    lateinit var tvTemp:TextView
    lateinit var tvWind:TextView
    lateinit var tvRain:TextView
    lateinit var tvDate:TextView
    lateinit var  imgMain :ImageView
    lateinit var tvDes:TextView
    lateinit var btShare:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        tvTemp = findViewById(R.id.tvTempMain)
        tvWind = findViewById(R.id.tvWindMain)
        tvRain = findViewById(R.id.tvRainMain)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // getting current location from the user

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the necessary permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }

        // Permissions are granted, so get the current location
        getCurrentLocation()


        // code for getting text written in search view




        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                stringSv = query
                wList.clear()
                fetchData()
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = layoutManager
                val adapter = MyAdapter(wList)
                recyclerView.adapter = adapter

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Perform filtering as the text changes

                return false
            }
        })



    }

    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Request the necessary permissions if not granted

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Get the latitude and longitude from the location object
                var lat = location?.latitude
                var long = location?.longitude

                Log.d("coordinates","$lat , $long")

                if (lat != null && long!=null) {
                    fetchData7Day(lat,long)
                }

                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.layoutManager = layoutManager
                val adapter = MyAdapter(wList)
                recyclerView.adapter = adapter



            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur while getting the location
                Toast.makeText(
                    this,
                    "Error getting location: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    fun fetchData() {
        // Api used = WeatherBit

        val api = "8fbb06b7a2a944458b97e3e8ce522882"
        var str =stringSv
        val url = "https://api.weatherbit.io/v2.0/forecast/daily?city=$str&key=$api"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->

                // if everything goes fine

                // get the JSON object
                val tempArray = response.getJSONArray("data")

                // get the JSON object from the
                // array at index position 0

                for (i in 0 until 7){

                    val obj2 = tempArray.getJSONObject(i)

                    // set the temperature and the city
                    // name using getString() function
                    // var strCity = response.getString("city_name").toString()
                    var strTemp = obj2.getString("temp").toString()
                    var strtemp = "$strTemp ° C"
                    var strDate = obj2.getString("datetime").toString()

                    var partDate = strDate.split("-")
                    var dateInFormat = "${partDate[1]}-${partDate[2]}-${partDate[0]}"

                    // var strDes= obj2.getJSONArray("weather").getJSONObject(0).getString("description")

                    // lets get the desciption data which is nested array


                    var obj = obj2.getJSONObject("weather")
                    var description = obj.getString("description").toString()

                    var wind  = obj2.getString("wind_cdir").toString()
                    var rain = obj2.getString("precip").toString()

                    if(i==0){
                        //tvDes.setText(description)
                        tvTemp.setText(strtemp)
                       // tvDate.setText(dateInFormat)

                        tvRain.setText(rain)
                        tvWind.setText(wind)

                        btShare = findViewById(R.id.button)

                        btShare.setOnClickListener {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Heyyy \n Date = $dateInFormat \n Temperature = $strtemp \n  " +
                                        "Description  = $description")
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(shareIntent)
                        }
                        //  tvCity.setText(strCity)

                    }

                    val listData = Weather(dateInFormat,strtemp,description,wind,rain)
                    wList.add(listData)

                    Log.d("weather",strDate)

                }

            },
            Response.ErrorListener { error ->

                // if error occurs

                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        )
        MySingelton.getInstance(this).addToRequestQueue(jsonObjectRequest)

//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        val layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
//        recyclerView.layoutManager = layoutManager
//
//        val adapter = MyAdapter(wList)
//
//        recyclerView.adapter = adapter

    }


    fun fetchData7Day(lat: Double,long: Double){

        //Toast.makeText(this," latitiude = $lat , longitude = $long ",Toast.LENGTH_LONG).show()

        val api = "8fbb06b7a2a944458b97e3e8ce522882"
        val url = "https://api.weatherbit.io/v2.0/forecast/daily?&lat=$lat&lon=$long&key=$api"

        wList.clear()
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->

                // if everything goes fine

                // get the JSON object
                val tempArray = response.getJSONArray("data")

                // get the JSON object from the
                // array at index position 0

                for (i in 0 until 7){

                    val obj2 = tempArray.getJSONObject(i)

                    // set the temperature and the city
                    // name using getString() function
                   // var strCity = response.getString("city_name").toString()
                    var strTemp = obj2.getString("temp").toString()
                    var strtemp = "$strTemp ° C"
                    var strDate = obj2.getString("datetime").toString()

                    var partDate = strDate.split("-")
                    var dateInFormat = "${partDate[1]}-${partDate[2]}-${partDate[0]}"

                    // var strDes= obj2.getJSONArray("weather").getJSONObject(0).getString("description")

                    // lets get the desciption data which is nested array


                    var obj = obj2.getJSONObject("weather")
                    var description = obj.getString("description").toString()

                    var wind  = obj2.getString("wind_cdir").toString()
                    var rain = obj2.getString("precip").toString()

                    if(i==0){
                        //tvDes.setText(description)
                        tvTemp.setText(strtemp)
                        //tvDate.setText(dateInFormat)

                        tvRain.setText(rain)
                        tvWind.setText(wind)
                        btShare = findViewById(R.id.button)

                        btShare.setOnClickListener {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Heyyy \n Date = $dateInFormat \n Temperature = $strtemp \n  " +
                                        "Description  = $description")
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(shareIntent)
                        }
                      //  tvCity.setText(strCity)

                    }



                    val listData = Weather(dateInFormat,strtemp,description,wind, rain)
                    wList.add(listData)

                    Log.d("weatherforecast",strDate)

                }

            },
            Response.ErrorListener { error ->

                // if error occurs

                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        )
        MySingelton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}

