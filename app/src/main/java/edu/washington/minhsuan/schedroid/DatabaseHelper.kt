package edu.washington.minhsuan.schedroid

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.DatabaseUtils
import android.util.Log


class DatabaseHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val TAG = "DatabaseHelper"
    private val createUserTable = ("CREATE TABLE IF NOT EXISTS $TABLE_NAME_USERS (" +
            "$COL_NAME TEXT," +
            "$COL_PHONE TEXT," +
            "$COL_USERNAME TEXT PRIMARY KEY," +
            "$COL_PASSWORD TEXT);")
    private val createEventsTable = ("CREATE TABLE IF NOT EXISTS $TABLE_NAME_EVENTS (" +
            "$COL_USERNAME TEXT REFERENCES $TABLE_NAME_USERS ($COL_USERNAME)," +
            "$COL_TITLE TEXT," +
            "$COL_DATE TEXT," +
            "$COL_TIME TEXT," +
            "$COL_DES TEXT," +
            "$COL_LONG FLOAT," +
            "$COL_LAT FLOAT," +
            "$COL_DAILY TEXT," +
            "PRIMARY KEY ($COL_USERNAME, $COL_DATE, $COL_TIME));")

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createUserTable)
        db.execSQL(createEventsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(user: User) {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_NAME, user.full_name)
        cv.put(COL_PHONE, user.phone_num)
        cv.put(COL_USERNAME, user.username)
        cv.put(COL_PASSWORD, user.password)

        var rowID = db.insert(TABLE_NAME_USERS, null, cv)

        if (rowID == -1L) {
            Log.v(TAG, "Failed to insert user into database!")
        } else {
            Log.v(TAG, "Successfully inserted user into database!")
        }
    }

    fun insertEvent(event: Event) {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_USERNAME, event.username)
        cv.put(COL_TITLE, event.title)
        cv.put(COL_DATE, event.date)
        cv.put(COL_TIME, event.time)
        cv.put(COL_DES, event.descript)
        cv.put(COL_LONG, event.longitude)
        cv.put(COL_LAT, event.latitude)
        cv.put(COL_DAILY, event.daily)

        var rowID = db.insert(TABLE_NAME_EVENTS, null, cv)

        if (rowID == -1L) {
            Log.v(TAG, "Failed to insert event into database!")
        } else {
            Log.v(TAG, "Successfully inserted event into database!")
        }
    }

    fun readUsers(): MutableList<User> {
        val userList: MutableList<User> = ArrayList()

        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM $TABLE_NAME_USERS;", null)
        if (result.moveToFirst()) {
            do {
                val user = User()
                user.full_name = result.getString(result.getColumnIndex(COL_NAME))
                user.phone_num = result.getString(result.getColumnIndex(COL_PHONE))
                user.username = result.getString(result.getColumnIndex(COL_USERNAME))
                user.password = result.getString(result.getColumnIndex(COL_PASSWORD))
                userList.add(user)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return userList
    }

    fun readEvents(): MutableList<Event> {
        val eventList: MutableList<Event> = ArrayList()

        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM $TABLE_NAME_EVENTS;", null)
        if (result.moveToFirst()) {
            do {
                val event = Event()
                event.username = result.getString(result.getColumnIndex(COL_USERNAME))
                event.title = result.getString(result.getColumnIndex(COL_TITLE))
                event.date = result.getString(result.getColumnIndex(COL_DATE))
                event.time = result.getString(result.getColumnIndex(COL_TIME))
                event.descript = result.getString(result.getColumnIndex(COL_DES))
                event.longitude = result.getFloat(result.getColumnIndex(COL_LONG))
                event.latitude = result.getFloat(result.getColumnIndex(COL_LAT))
                event.daily = result.getInt(result.getColumnIndex(COL_DAILY))
                eventList.add(event)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return eventList
    }

    fun readEvents(username: String, date: String): MutableList<Event> {
        val eventList: MutableList<Event> = ArrayList()

        val db = this.readableDatabase
        val result = db.query(TABLE_NAME_EVENTS, arrayOf(COL_USERNAME, COL_TITLE, COL_DATE, COL_TIME, COL_DES,
            COL_LONG, COL_LAT, COL_DAILY), "username=? AND date=?", arrayOf(username, date),null,
            null, COL_TIME)
        if (result.moveToFirst()) {
            do {
                val event = Event()
                event.username = result.getString(result.getColumnIndex(COL_USERNAME))
                event.title = result.getString(result.getColumnIndex(COL_TITLE))
                event.date = result.getString(result.getColumnIndex(COL_DATE))
                event.time = result.getString(result.getColumnIndex(COL_TIME))
                event.descript = result.getString(result.getColumnIndex(COL_DES))
                event.longitude = result.getFloat(result.getColumnIndex(COL_LONG))
                event.latitude = result.getFloat(result.getColumnIndex(COL_LAT))
                event.daily = result.getInt(result.getColumnIndex(COL_DAILY))
                eventList.add(event)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return eventList
    }

    fun deleteEvent(username: String, date: String, time: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME_EVENTS, "username=? AND date=? AND time=?", arrayOf(username, date, time))
        db.close()
    }

    fun deleteAll(table: String) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $table;")
        db.close()
    }

    fun isExistUser(username: String): Boolean {
        val db = this.readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, TABLE_NAME_USERS, "username=?", arrayOf(username))
        db.close()
        return count != 0L
    }

    fun isCorrectPassword(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val result = db.query(TABLE_NAME_USERS, arrayOf(COL_PASSWORD), "username=?", arrayOf(username),
            null, null, null)
        var storedPass = ""
        if (result.moveToFirst()) {
            storedPass = result.getString(result.getColumnIndex(COL_PASSWORD))
        }
        result.close()
        db.close()
        return storedPass == password
    }

    fun getName(username: String): String {
        val db = this.readableDatabase
        val result = db.query(TABLE_NAME_USERS, arrayOf(COL_NAME), "username=?", arrayOf(username),
            null, null, null)
        var name = ""
        if (result.moveToFirst()) {
            name = result.getString(result.getColumnIndex(COL_NAME))
        }
        result.close()
        db.close()
        return name
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ScheDB"

        val TABLE_NAME_USERS = "Users"
        val COL_NAME = "full_name"
        val COL_PHONE = "phone_num"
        val COL_USERNAME = "username"
        val COL_PASSWORD = "password"

        val TABLE_NAME_EVENTS = "Events"
        val COL_TITLE = "title"
        val COL_DATE = "date"
        val COL_TIME = "time"
        val COL_DES = "description"
        val COL_LONG = "longitude"
        val COL_LAT = "latitude"
        val COL_DAILY = "daily"
    }
}