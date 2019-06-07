package edu.washington.minhsuan.schedroid

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable


class CreateNewEventFragment : Fragment() {
    val TAG = "CreateNewEventFragment"
    var username : String? = null
    var date : String? = null
    var isupdate = false
    var event : Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("user")
            date = it.getString("day")
            isupdate = it.getBoolean("isUpdate")
            event = if (it.getSerializable("Event") is String) { null } else { it.getSerializable("Event") as Event }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.create_new_event, container, false)
        val db = DatabaseHelper(context!!)

        val title = rootView.findViewById<TextView>(R.id.txt_create_title)
        var inputTitle = rootView.findViewById<EditText>(R.id.edit_title)
        var inputTime = rootView.findViewById<EditText>(R.id.edit_time)
        var inputDescription = rootView.findViewById<EditText>(R.id.edit_description)
        var inputDaily = rootView.findViewById<EditText>(R.id.edit_Y_or_N)
        val btn_map = rootView.findViewById<Button>(R.id.btn_open_map)
        var btnOk = rootView.findViewById<Button>(R.id.btn_ok)

        if (isupdate) {
            title.text = "Update Current Event:"
            btnOk.text = "Update!"
            inputTitle.setText(event!!.title)
            inputTime.setText(event!!.time)
            inputDescription.setText(event!!.descript)
            App.instance.repo.currentLoc= LatLng(event!!.longitude.toDouble(), event!!.latitude.toDouble())
            val daily = if (event!!.daily == 1) { "Y" } else { "N" }
            inputDaily.setText(daily)
        }

        // TODO: open map
        btn_map.setOnClickListener{
            val openMap = Intent(activity, MapsActivity::class.java)
            startActivity(openMap)
        }

        btnOk.setOnClickListener {
            if (isupdate) {
                db.deleteEvent(event!!.username, event!!.date, event!!.time)
            }
            inputTitle = rootView.findViewById(R.id.edit_title)
            inputTime = rootView.findViewById(R.id.edit_time)
            inputDescription = rootView.findViewById(R.id.edit_description)
            inputDaily = rootView.findViewById(R.id.edit_Y_or_N)
            var daily = if (inputDaily.text.toString().toLowerCase() == "y") { 1 } else { 0 }

            var error = ""
            if (inputTitle.text.isEmpty()) { error = "$error Please enter an event title;\n"}
            if (inputTime.text.isEmpty()) { error = "$error Please enter the time of the event;\n"}
            if (inputDescription.text.isEmpty()) { error = "$error Please enter a description;\n" }
            if (inputDaily.text.isEmpty()) { error = "$error Please enter Y/N for Daily;\n" }
            if (App.instance.repo.currentLoc==null) {error = "$error Please assign a location;\n"}

            if (error.isNotEmpty()) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
            } else {
                val eventObject = Event(username!!, inputTitle.text.toString(), date!!, inputTime.text.toString(),
                    inputDescription.text.toString(), App.instance.repo.currentLoc!!.longitude.toFloat(), App.instance.repo.currentLoc!!.latitude.toFloat(), daily)
                db.insertEvent(eventObject)
                val message = if (isupdate) { "Event Updated!" } else { "Event created!" }
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                val fragmentManager = fragmentManager
                val onedayFragment = OnedayFragment.newInstance(username!!, date!!)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container, onedayFragment, "ONE_DAY_FRAGMENT")
                    .addToBackStack(null)
                    .commit()
            }
            App.instance.repo.currentLoc = null
        }

        return rootView
    }

    companion object {
        fun newInstance(userName: String, day: String, update: Boolean, event: Serializable) =
            CreateNewEventFragment().apply {
                arguments = Bundle().apply {
                    putString("user", userName)
                    putString("day", day)
                    putBoolean("isUpdate", update)
                    putSerializable("Event", event)
            }
        }
    }
}