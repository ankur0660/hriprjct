package com.example.demoapp.Activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.example.demoapp.Constants.Constants
import com.example.demoapp.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_employer_main.*
import kotlinx.android.synthetic.main.activity_employer_main.etCities
import kotlinx.android.synthetic.main.activity_employer_main.etStates

import kotlinx.android.synthetic.main.item_row.view.*
import kotlinx.android.synthetic.main.worker_address_picker_layout.*
import okhttp3.*
import java.io.IOException


// Activity to show the employer the workers he wants to hire.

class EmployerMainActivity : AppCompatActivity() {

    var adapterList = ArrayList<Record>()
    val adapter = MyAdapter(adapterList)
    var list = ArrayList<PersonDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer_main)
        fillStatesAndCities()
        var sharedPreferences = getSharedPreferences("demopref", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("uid", null)
        if (uid == null) {
            val intent = Intent(baseContext, PhoneNumberAuthentication::class.java)
            intent.putExtra("usertype", Constants.EMPLOYER)
            startActivity(intent)
            finish()
        }
        etcategory.setOnClickListener {
            populate()
        }

        etcategory.setOnFocusChangeListener { view: View, b: Boolean ->
            populate()
        }
        val layoutManager = LinearLayoutManager(baseContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv.layoutManager = layoutManager
        rv.adapter = adapter
        adapterList.add(Record("dcs" , "csdcd" , 3f))
        adapter.notifyDataSetChanged()
        search_button.setOnClickListener {
            fetchDataFromDatabase(etCities.text.toString(), etcategory.text.toString())
        }
    }

    private fun fetchDataFromDatabase(cities: String, category: String) {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
            .child(cities).child("workers").child(category)

        firebaseDatabase.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val personDetails = dataSnapshot.getValue(PersonDetails::class.java)
                list.add(personDetails!!)
                Log.e("Kya aaya" , list.toString())

                var recordList = makeListOfRecords(list)
                Log.e("yebhi" , recordList.toString())
                if (recordList.isEmpty()) {
                    Toast.makeText(baseContext , "khali hai" , Toast.LENGTH_SHORT).show()
                } else {
                    runOnUiThread{
                        Log.e("kya ho raha" , "chal ja yrr")
                        adapterList.addAll(recordList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun makeListOfRecords(list: ArrayList<PersonDetails>): ArrayList<Record> {
        val listrecord = ArrayList<Record>()
        list.forEach {
            val name = it.FirstName + " " + it.LastName
            val age = findAge(it.DateOfBirth)
            val random = (0..5).random().toFloat()

            val record = Record(name, age, random)
            listrecord.add(record)
        }

        return listrecord
    }

    private fun fillStatesAndCities() {

        val okHttpClient = OkHttpClient()

        val request = Request.Builder().url(Constants.URL).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonstring = response.body()?.string()
                Log.e("JSONString", jsonstring)
                val gson = Gson()
                val type = object : TypeToken<ArrayList<State>>() {}.type
                val list = gson.fromJson<ArrayList<State>>(jsonstring, type)
                populateStatesAndCities(list)

            }

        })
    }


    private fun populateStatesAndCities(statesAndCities: ArrayList<State>) {
        val states = ArrayList<String>()
        for (i in 0..statesAndCities.size - 1) {
            states.add(statesAndCities[i].state)
        }
        Log.e("States", states.toString())

        runOnUiThread {
            etStates.setOnClickListener {
                popUpStateDialog(states)
            }

            etStates.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
                if (hasFocus) {
                    popUpStateDialog(states)
                }
            }

            etCities.setOnClickListener {
                popUpCitiesDialog(states, statesAndCities)
            }

            etCities.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
                if (hasFocus) {
                    popUpCitiesDialog(states, statesAndCities)
                }
            }

        }

    }

    private fun popUpCitiesDialog(states: ArrayList<String>, statesAndCities: ArrayList<State>) {
        val state = etStates.text.toString()
        var pos: Int = 0
        for (i in 0..states.size - 1) {
            if (states[i].equals(state)) {
                pos = i
            }
        }

        val cities = statesAndCities[pos].cities
        val arrayAdapter = ArrayAdapter<String>(baseContext, android.R.layout.simple_list_item_1, cities)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Your City")
            .setAdapter(arrayAdapter) { dialogInterface: DialogInterface, which: Int ->
                etCities.setText(cities?.get(which))
            }.create().show()

    }

    private fun popUpStateDialog(states: ArrayList<String>) {
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, states)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Your State")
            .setAdapter(arrayAdapter) { dialogInterface: DialogInterface, which: Int ->
                etStates.setText(states[which])
            }.create().show()
    }

    private fun populate() {
        val list = ArrayList<String>()
        list.add("Maid")
        list.add("Masson(House Labour)")
        list.add("Electrician")
        list.add("Babysitter")
        list.add("Architect")
        list.add("Plumber")
        val arrayAdapter = ArrayAdapter<String>(baseContext, android.R.layout.simple_list_item_1, list)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Your Job")
            .setAdapter(arrayAdapter) { dialogInterface: DialogInterface, which: Int ->
                etcategory.setText(list[which])
            }.create().show()
    }


}


class MyAdapter(var list: ArrayList<Record>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val context = p0.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.item_row, p0, false)
        val viewholder = MyViewHolder(view)
        return viewholder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val record = list.get(p1)

        val viewholder = p0 as MyViewHolder

        viewholder.tvName.text = record.name
        viewholder.tvAge.text = record.age
        viewholder.ratingBar.rating = record.Ratings
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tvName: TextView
        lateinit var tvAge: TextView
        lateinit var ratingBar: RatingBar

        init {
            tvName = view.tvName
            tvAge = view.tvAge
            ratingBar = view.ratingBar
        }
    }

}

class Record(
    var name: String,
    var age: String,
    var Ratings: Float
)