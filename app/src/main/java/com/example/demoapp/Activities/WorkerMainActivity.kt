package com.example.demoapp.Activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import com.example.demoapp.Constants.Constants
import com.example.demoapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_worker_layout.*

import java.util.*

//// Activity to show the worker the details provided by him during the sign up

class WorkerMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_layout)
        val sharedPreferences = getSharedPreferences("demopref" , Context.MODE_PRIVATE)
        val alreadysignedin = sharedPreferences.getBoolean("signedinstatus" , false)
        val uid = sharedPreferences.getString("uid" , null)
        val category = sharedPreferences.getString("category" , null)
        val city = sharedPreferences.getString("city" , null)
        if(alreadysignedin){
            showDetailsOfWorker(uid , category , city)
        }

        else{
            val intent = Intent(baseContext , WorkerPersonalDetailsPickerActivity::class.java)
            intent.putExtra("usertype" , Constants.WORKER)
            startActivity(intent)
            finish()
        }
    }

    private fun showDetailsOfWorker(uid : String , category: String , city : String) {
        // code to fetch data from the firebase database and display in the worker activity
        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child(city).child("workers")
            .child(category)
            .child(uid).addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(data : DataSnapshot) {
                    val personDetails = data.getValue(PersonDetails::class.java)
                    tvdob.text = personDetails?.DateOfBirth
                    name.text = personDetails?.FirstName + " " + personDetails?.LastName
                    tvaddress.text = personDetails?.Landmark + ", " + personDetails?.City + ", " + personDetails?.State
                    tvAge.text =findAge(personDetails?.DateOfBirth.toString())
                    Picasso.get().load(personDetails?.image_url).into(detail_profile_image)
                    layout.visibility = View.GONE
                }

            })
    }

}

public fun findAge(dob : String?) : String{
    val temp = dob?.substring(dob.length - 4)
    val year = temp?.toInt()

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()

    val age = calendar.get(Calendar.YEAR) - year!!
    return age.toString()
}
