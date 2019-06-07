package edu.washington.minhsuan.schedroid

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.Serializable


class OnedayFragment: Fragment() {
    private val TAG = "OnedayFragment"
    private var username: String = ""
    private var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("user")
            date = it.getString("day")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.one_day_view, container, false)
        val db = DatabaseHelper(context!!)
        val fragmentManager = fragmentManager

        val title = rootView.findViewById<TextView>(R.id.txt_title)
        val createBtn = rootView.findViewById<Button>(R.id.btn_addNew)
        val list = rootView.findViewById<ListView>(R.id.display_oneday_list)

        title.text = "Schedule for $date"

        var dataEvents = db.readEvents(username, date)
        var events = arrayListOf<String>()
        for (i in 0 until (dataEvents.size)) {
            events.add(dataEvents[i].toString())
        }

        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, events)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->
            val event = Event(parent.getItemAtPosition(position).toString())

            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Edit Event")
            builder.setMessage("What would you like to do?")

            builder.setPositiveButton("Edit"){ _, _ ->
                val createEventFragment = CreateNewEventFragment.newInstance(username, date,
                    true, event as Serializable)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container, createEventFragment, "CREATE_EVENT_FRAGMENT")
                    .addToBackStack(null)
                    .commit()
            }

            builder.setNegativeButton("Delete"){ _,_ ->
                db.deleteEvent(username, date, event.time)

                val onedayFragment = newInstance(username, date)
                fragmentManager!!.beginTransaction()
                    .replace(R.id.container, onedayFragment, "ONE_DAY_FRAGMENT")
                    .addToBackStack(null)
                    .commit()
            }

            builder.setNeutralButton("Cancel"){ _,_ ->
                Toast.makeText(context!!,"OK. Never mind.", Toast.LENGTH_SHORT).show()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        createBtn.setOnClickListener{
            val createEventFragment = CreateNewEventFragment.newInstance(username, date,
                false, "")
            fragmentManager!!.beginTransaction()
                .replace(R.id.container, createEventFragment, "CREATE_EVENT_FRAGMENT")
                .addToBackStack(null)
                .commit()
        }

        return rootView
    }

    companion object {
        fun newInstance(user: String, date: String) = OnedayFragment().apply {
            arguments = Bundle().apply {
                putString("user", user)
                putString("day", date)
            }
        }
    }
}