package com.example.demoapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.demoapp.Constants.Constants
import com.example.demoapp.R
import kotlinx.android.synthetic.main.account_chooser.*

class Account_chooser_activity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_chooser)

        worker_button.setOnClickListener{
            launchAuthenticationActivity(Constants.WORKER)
        }

        employer_button.setOnClickListener{
            launchAuthenticationActivity(Constants.EMPLOYER)
        }
    }

    private fun launchAuthenticationActivity(usertype: String) {
        val intent = Intent(baseContext , PhoneNumberAuthentication::class.java)
        intent.putExtra("usertype" , usertype)
        startActivity(intent)
        finish()
    }

}