package com.example.demoapp.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import com.example.demoapp.Constants.Constants
import com.example.demoapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.login.*
import java.util.concurrent.TimeUnit

class PhoneNumberAuthentication : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val usertype = intent.getStringExtra("usertype")
        sendotp.setOnClickListener{
            if(validPhoneNumber(etPhoneNumber_login.text.toString())){
                val phone_number = "+91" + etPhoneNumber_login.text.toString()
                val intent = Intent(baseContext , OTPVerify::class.java)
                intent.putExtra("usertype" , usertype)
                intent.putExtra("Number" , phone_number)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validPhoneNumber(number: String): Boolean {
        return number.matches(Regex("^\\d{10}+\$"))
    }
}
