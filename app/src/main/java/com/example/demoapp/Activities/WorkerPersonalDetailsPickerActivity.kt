package com.example.demoapp.Activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.demoapp.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.worker_personal_details_picker_activity.*
import java.util.*


// Activity to pick the worker personal details including name , date of birth , etc.

class WorkerPersonalDetailsPickerActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 0

    var PROFILE_PHOTO_URI : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.worker_personal_details_picker_activity)

        profile_photo.setOnClickListener {
            choosePhoto()
        }



        etdate.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (hasFocus) {
                displayDatePicker()
            }
        }
        etdate.setOnClickListener() {
            displayDatePicker()
        }

        continue_button.setOnClickListener {
            if (validateCredentials()) {
                val intent = Intent(baseContext, WorkersAddressPickerActivity::class.java)
                intent.putExtra("firstname", etfirstName.text.toString())
                intent.putExtra("lastname", etlastName.text.toString())
                intent.putExtra("dateofbirth", etdate.text.toString())
                intent.putExtra("uri" , PROFILE_PHOTO_URI)
                startActivity(intent)
            }
        }

    }

    private fun displayDatePicker() {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val dob = DatePickerDialog(
            this,
            R.style.DialogTheme,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                etdate.setText("${dayOfMonth.toString()} ${monthMapping(month)} ${year.toString()}")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            val uriPhoto = data?.data
            profile_photo.setImageURI(uriPhoto)
            PROFILE_PHOTO_URI = uriPhoto.toString()
        }
    }

    private fun validateCredentials(): Boolean {

        if (!ContainsAplhabetsOnly(etfirstName.text.toString())) {
            Toast.makeText(baseContext, "Name should contain only letters", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (etfirstName.text.isNullOrEmpty()) {
            Toast.makeText(baseContext, "Please Fill Your First Name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!ContainsAplhabetsOnly(etlastName.text.toString())) {
            Toast.makeText(baseContext, "Last Name should contain only letters", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (etlastName.text.isNullOrEmpty()) {
            Toast.makeText(baseContext, "Please Fill Your Last Name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!ContainsAplhabetsOnly(etlastName.text.toString())) {
            Toast.makeText(baseContext, "Name should contain only letters", Toast.LENGTH_SHORT).show()
            return false
        } else if (etdate.text.toString().equals("SELECT YOUR DOB")) {
            Toast.makeText(baseContext, "Please Choose Your Date of Birth", Toast.LENGTH_SHORT).show()
            return false
        }

        if(findAge(etdate.text.toString()).toInt() <= 18){
            Toast.makeText(baseContext, "Your Age is less than 18", Toast.LENGTH_SHORT).show()
            return false
        }

        if(PROFILE_PHOTO_URI == null){
            Toast.makeText(baseContext, "Please Choose Profile Image", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun ContainsAplhabetsOnly(name: String): Boolean {
        return name.matches(Regex("^[a-zA-z]*\$"))
    }
}


fun monthMapping(month: Int): String {
    return when (month) {
        0 -> "JAN"
        1 -> "FEB"
        2 -> "MAR"
        3 -> "APR"
        4 -> "MAY"
        5 -> "JUN"
        6 -> "JUL"
        7 -> "AUG"
        8 -> "SEP"
        9 -> "OCT"
        10 -> "NOV"
        else -> "DEC"
    }
}
