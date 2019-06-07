package edu.washington.minhsuan.schedroid

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest as Manifest
import android.Manifest.permission
import android.Manifest.permission.READ_CONTACTS
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import com.google.android.gms.common.api.GoogleApiClient
import android.widget.TextView
import android.content.Context.LOCATION_SERVICE
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.PointOfInterest
import java.util.*


//mMapFragment = MapFragment.newInstance();
//FragmentTransaction fragmentTransaction =
//getFragmentManager().beginTransaction();
//fragmentTransaction.add(R.id.my_container, mMapFragment);
//fragmentTransaction.commit();

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener ,
    GoogleMap.OnMapClickListener, GoogleMap.OnPoiClickListener {

    private lateinit var mMap: GoogleMap

    val TAG = "Bonan Kou"
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        Log.e(TAG, "here")
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            Toast.makeText(this, "回到Event界面", Toast.LENGTH_SHORT).show()
        }

        //searchBar

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
        mMap.setOnMapClickListener(this);
        mMap.setOnPoiClickListener(this)
        mMap.setOnMapLongClickListener(this);
        App.instance.repo.mMap = googleMap
        //if (isAdded())
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "fuck")
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }

        } else {
            mMap.isMyLocationEnabled = true
        }
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        //开始准备调角度
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val longitude = location.longitude
        val latitude = location.latitude
        val mLocation = LatLng(latitude, longitude)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 10f))
        Log.e(TAG, mLocation.toString())
        Log.e("dfjdlkfj", latitude.toString())
        val test = LatLng(0.0,0.0)



        // Create persistent LocationManager reference


            try {
                // Request location updates
                lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, App.instance.repo.locationListener);
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available");
            }


    }


    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults:IntArray) {
            if (requestCode == 1/*whatever you set in requestPermissions()*/) {
                if (!grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_CONTACTS
                        ) != PackageManager.PERMISSION_GRANTED
                    )
                        Log.e(TAG, "woaixumiao")
                    mMap.isMyLocationEnabled = true
                }
            }
        }

    override fun onMapLongClick(p0: LatLng?) {
        App.instance.repo.currentLoc = p0
        Log.e("Kou", "IloveRuoqing")
        mMap.clear()
        val marker = mMap!!.addMarker(MarkerOptions().position(p0!!).title("Selected Location"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(p0, 15f))
         val builder = AlertDialog.Builder(this)
                builder.apply {
                    setTitle("Selected Location")
                    setMessage("Set this as the desired destination?")
                    setPositiveButton("Yes") {dialog, id ->
                        App.instance.repo.currentLoc=p0
                        Toast.makeText(this@MapsActivity, "Location selected", Toast.LENGTH_SHORT).show()
                    }
                    setNegativeButton("No") {dialog, which ->
                        Toast.makeText(this@MapsActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                        marker.remove()
                        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
                        if (ContextCompat.checkSelfPermission(this@MapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            val longitude = location.longitude
                            val latitude = location.latitude
                            val mLocation = LatLng(latitude,longitude)
                            Log.e("Bonan", "IloveSu")
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 15f))
                        }
                    }
                }
                val dialog = builder.create()
                dialog.show()
    }

    override fun onMapClick(p0: LatLng?) {
        Log.e("Kou", "Iloveyuhan")

    }

    override fun onPoiClick(poi: PointOfInterest?) {

        Toast.makeText(getApplicationContext(), "Clicked: " +
                poi!!.name + "\nPlace ID:" + poi!!.placeId +
                "\nLatitude:" + poi!!.latLng.latitude +
                " Longitude:" + poi!!.latLng.longitude,
            Toast.LENGTH_SHORT).show()
        mMap.clear()
        val marker = mMap!!.addMarker(MarkerOptions().position(poi.latLng!!).title("Selected Location"))
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(poi!!.name)
            setMessage("Set this as the desired destination?")
            setPositiveButton("Yes") {dialog, id ->
                App.instance.repo.currentLoc=poi!!.latLng
                App.instance.repo.currentPoi=poi!!.name
                Toast.makeText(this@MapsActivity, App.instance.repo.currentPoi + " selected", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("No") {dialog, which ->
                Toast.makeText(this@MapsActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                marker.remove()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
    }
