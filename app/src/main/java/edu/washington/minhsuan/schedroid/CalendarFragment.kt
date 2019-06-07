package edu.washington.minhsuan.schedroid

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CalendarFragment: Fragment() {
//    companion object {
//        fun newInstance(topic:Int, total:Int, correct:Int):QuizFragment {
//            val args = Bundle()
//            args.apply{
//                putInt("correct", correct)
//                putInt("total", total)
//                putInt("topic", topic)
//            }
//            val result = QuizFragment()
//            result.arguments = args
//            return result
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootview = inflater.inflate(R.layout.calendar, container, false)
        val calendarView = rootview.findViewById<CalendarView>(R.id.calendar)
        calendarView.setOnDateChangeListener { view:View, year:Int, month:Int, dayOfMonth:Int ->
            val date = String.format("%02d", month+1)+"-"+String.format("%02d", dayOfMonth)+"-"+year.toString()
            val onedayFragment = OnedayFragment.newInstance(App.instance.repo.currentName!!, date)
            fragmentManager!!.beginTransaction()
                .replace(R.id.container, onedayFragment, "ONE_DAY_FRAGMENT")
                .addToBackStack(null)
                .commit()
            Toast.makeText(context, "" + dayOfMonth, Toast.LENGTH_SHORT).show()// TODO Auto-generated method stub
        }
        return rootview
    }

}
