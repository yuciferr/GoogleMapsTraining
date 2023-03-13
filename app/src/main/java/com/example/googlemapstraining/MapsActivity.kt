package com.example.googlemapstraining

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemapstraining.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationManager: LocationManager
    private lateinit var  locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)

        // Add a marker in Sydney and move the camera
        /*
        val ankara = LatLng(39.92, 32.84)
        mMap.addMarker(MarkerOptions().position(ankara).title("Marker in Ankara"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ankara,15f))*/

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {
                mMap.clear()
                val location = LatLng(p0.latitude,p0.longitude)
                mMap.addMarker(MarkerOptions().position(location).title("You are Here"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15f))

            }
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)

        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,10f,locationListener)
            val latest = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(latest != null){
                val latestLatLng = LatLng(latest.latitude,latest.longitude)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latestLatLng).title("Your last location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestLatLng,15f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,10f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val listener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng) {
            mMap.clear()

            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

            if(p0 != null){

                var address = ""
                try {
                    val addressList = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if(addressList?.size!! > 0){

                        if(addressList[0].thoroughfare != null) {
                            address += addressList[0].thoroughfare
                            if(addressList[0].subThoroughfare != null) {
                                address += addressList[0].subThoroughfare

                            }
                        }
                    }

                } catch (e: Exception){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(address))
            }

        }
    }
}