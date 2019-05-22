package com.example.demoapp.Activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.demoapp.Constants.Constants.Companion.URL
import com.example.demoapp.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.worker_address_picker_layout.*
import okhttp3.*
import java.io.IOException


// Activity to pick the address details of the worker.

class WorkersAddressPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.worker_address_picker_layout)
        fillStatesAndCities()
        populateCategory()
        button_continue.setOnClickListener {
            if (validateCredentials()) {
                uploadDataToFirebase()
            }
        }
    }

    private fun populateCategory() {

        categories.setOnClickListener{
            populate()
        }

        categories.setOnFocusChangeListener{ view: View, hasFocus: Boolean ->
            if(hasFocus){
                populate()
            }
        }
    }

    private fun populate(){
        val list = ArrayList<String>()
        list.add("Maid")
        list.add("Masson(House Labour)")
        list.add("Electrician")
        list.add("Babysitter")
        list.add("Architect")
        list.add("Plumber")
        val arrayAdapter = ArrayAdapter<String>(baseContext , android.R.layout.simple_list_item_1 , list)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Your Job")
            .setAdapter(arrayAdapter) { dialogInterface: DialogInterface, which : Int ->
                categories.setText(list[which])
            }.create().show()
    }

    private fun uploadDataToFirebase() {


        val firstname = intent.getStringExtra("firstname")
        val lastname = intent.getStringExtra("lastname")
        val dob = intent.getStringExtra("dateofbirth")
        val uriphoto = intent.getStringExtra("uri")
        val state = etStates.text.toString()
        val city = etCities.text.toString()
        val street = etstreet.text.toString()
        val sharedPreferences = getSharedPreferences("demopref", Context.MODE_PRIVATE)
        val phoneNumber = sharedPreferences.getString("phonenumber", null)
        val uid = sharedPreferences.getString("uid", null)
        val category = categories.text.toString()
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
            .child(city)
            .child("workers")
            .child(category)

        layout.visibility = View.VISIBLE
        val firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://demoapp-3afde.appspot.com")
        firebaseStorage.child(uid).putFile(Uri.parse(uriphoto)).addOnSuccessListener {
            Toast.makeText(baseContext, "Image uploaded", Toast.LENGTH_SHORT).show()
            firebaseStorage.child(uid).downloadUrl.addOnSuccessListener {
                val personDetails = PersonDetails(firstname, lastname, dob, street, state, city, phoneNumber, it.toString())
                firebaseDatabase.child(uid).setValue(personDetails)
                    .addOnSuccessListener {
                        Toast.makeText(baseContext, "Details submitted successfully", Toast.LENGTH_SHORT).show()
                        sharedPreferences.edit().putBoolean("signedinstatus" , true)
                            .putString("category" , category)
                            .putString("city" , city)
                            .apply()
                        val intent = Intent(baseContext , WorkerMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(baseContext, it.message.toString() , Toast.LENGTH_SHORT).show()
                    }
            }
        }



    }

    private fun validateCredentials(): Boolean {
        if (etstreet.text.toString().isNullOrEmpty()) {
            Toast.makeText(baseContext, "Please fill your Landmark or Area", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etStates.text.toString().equals("Choose Your State")) {
            Toast.makeText(baseContext, "Please choose your State", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etCities.text.toString().equals("Choose a City")) {
            Toast.makeText(baseContext, "Please choose your City", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun fillStatesAndCities() {

        val okHttpClient = OkHttpClient()

        val request = Request.Builder().url(URL).build()
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
            etStates.setOnClickListener{
                popUpStateDialog(states)
            }

            etStates.setOnFocusChangeListener{ view: View, hasFocus: Boolean ->
                if(hasFocus){
                    popUpStateDialog(states)
                }
            }

            etCities.setOnClickListener{
                popUpCitiesDialog(states , statesAndCities)
            }

            etCities.setOnFocusChangeListener{ view: View, hasFocus: Boolean ->
                if(hasFocus){
                    popUpCitiesDialog(states , statesAndCities)
                }
            }

        }

    }

    private fun popUpCitiesDialog(states: ArrayList<String>, statesAndCities: ArrayList<State>){
        val state = etStates.text.toString()
        var pos : Int = 0
        for( i in 0..states.size - 1){
            if(states[i].equals(state)){
                pos = i
            }
        }

        val cities = statesAndCities[pos].cities
        val arrayAdapter = ArrayAdapter<String>(baseContext ,android.R.layout.simple_list_item_1 , cities)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Your City")
            .setAdapter(arrayAdapter) { dialogInterface: DialogInterface, which : Int ->
                etCities.setText(cities?.get(which))
            }.create().show()

    }

    private fun popUpStateDialog(states : ArrayList<String>){
        val arrayAdapter = ArrayAdapter<String>(this ,android.R.layout.simple_list_item_1 , states)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Your State")
            .setAdapter(arrayAdapter) { dialogInterface: DialogInterface, which : Int ->
                etStates.setText(states[which])
            }.create().show()
    }
}




class Person(
    uid: String,
    details: PersonDetails
)

class PersonDetails(
    var FirstName: String = "Anonymous",
    var LastName: String = "",
    var DateOfBirth: String = "1 JAN 1900",
    var Landmark: String = "",
    var State: String = "Anonymous_city",
    var City: String = "Anonymous_state",
    var PhoneNumber: String = "1111111111",
    var image_url: String = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
)


class State(var state: String = "", var cities: ArrayList<String>? = null)