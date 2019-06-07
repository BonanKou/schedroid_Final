package edu.washington.minhsuan.schedroid

import android.util.Log
import java.io.Serializable

class Event: Serializable {
    private val TAG = "Event"

    var username: String = ""
    var title: String = ""
    var date: String = ""
    var time: String = ""
    var descript: String = ""
    var longitude: Float = 0f
    var latitude: Float = 0f
    var daily: Int = 0

    constructor()

    constructor(username: String, title: String, date: String, time: String, descript: String, longitude: Float,
                latitude: Float, daily: Int) {
        this.username = username
        this.title = title
        this.date = date
        this.time = time
        this.descript = descript
        this.longitude = longitude
        this.latitude = latitude
        this.daily = daily
    }

    constructor(text: String) {
        val text1 = text.split(", ")
        var text2 = arrayListOf<String>()
        for (s in text1) {
            text2.add(s.split(": ")[1])
        }
        this.username = text2[0]
        this.title = text2[1]
        this.date = text2[2]
        this.time = text2[3]
        this.descript = text2[4]
        this.longitude = text2[5].toFloat()
        this.latitude = text2[6].toFloat()
        this.daily = text2[7].toInt()
    }

    override fun toString(): String {
        return "username: $username, title: $title, date: $date, time: $time, description: $descript, " +
                "longitude: $longitude, latitude: $latitude, daily: $daily"
    }

}