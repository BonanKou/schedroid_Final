package edu.washington.minhsuan.schedroid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Date
interface repo {

    //return a list of Events
    fun getEvents(date:Date):ArrayList<Event>?
    fun addEvent(date:Date, event:Event)
    fun removeEvent(date:Date, num:Int)
}
class repository(): repo {
    var mMap:GoogleMap? = null
                                                                                                val locationListener: LocationListener = object : LocationListener {

                                                                                                    override fun onLocationChanged(location: Location) {

                                                                                                        //mMap!!.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title((location.latitude).toString()))
                                                                                                        //Toast.makeText(, "changed", Toast.LENGTH_LONG).show()
                                                                                                        Log.e("Bonan", "Ilovexumiao")
                                                                                                    }
                                                                                                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                                                                                                    override fun onProviderEnabled(provider: String) {}
                                                                                                    override fun onProviderDisabled(provider: String) {}
                                                                                                }
    var map:MutableMap<Date, ArrayList<Event>> = mutableMapOf()
    var currentLoc:LatLng? = null
    var currentPoi:String? = null
    var currentName:String? = null
    var currentTime:String? = null
    var currentTitle:String? = null
    var currentDescription: String? = null
    var currentDaily:Int? = null
    var currentDate:String? = null
    var currentUsername:String? = null


    //return某一天所有event
    override fun getEvents(date:Date):ArrayList<Event>? {
        if (map != null) {
            if (map!!.containsKey(date)) {
                return map.get(date)
            }
            //wenren
        }
        return null
    }

    //在某日加入一个新event
    override fun addEvent(date: Date, event:Event) {
            if (map.get(date) != null) {
                (map.get(date))!!.add(event)
            } else {
                map.put(date, arrayListOf<Event>(event))
            }
    }

    //使用hashcode来从某日移除某个特别的event
    override fun removeEvent(date: Date, num: Int) {
        if (map.get(date) != null) {
            val events = map.get(date)
            for (i in 0 until (events!!.size-1)) {
                if (events[i].hashCode() == num) {
                    events.remove(events[i])
                }
            }
        }
    }
}


class App: Application() {

    lateinit var repo: repository
        private set

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        Log.e("BonanKou", "Success")
        super.onCreate()
        repo = repository()
        instance = this
    }
}