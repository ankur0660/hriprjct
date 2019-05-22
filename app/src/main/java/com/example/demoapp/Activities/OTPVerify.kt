package com.example.demoapp.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import com.example.demoapp.Constants.Constants
import com.example.demoapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.otp_layout.*
import java.util.concurrent.TimeUnit

class OTPVerify : AppCompatActivity() {

    lateinit var usertype: String
    private lateinit var otpEt1: EditText
    private lateinit var otpEt2: EditText
    private lateinit var otpEt3: EditText
    private lateinit var otpEt4: EditText
    private lateinit var otpEt5: EditText
    private lateinit var otpEt6: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_layout)
       ///////////////ankur////////////////////////
        usertype = intent.getStringExtra("usertype")
        otpEt1 = findViewById(R.id.opt1) as EditText
        otpEt2 = findViewById(R.id.opt2) as EditText
        otpEt3 = findViewById(R.id.opt3) as EditText
        otpEt4 = findViewById(R.id.opt4) as EditText
        otpEt5 = findViewById(R.id.opt5) as EditText
        otpEt6 = findViewById(R.id.opt6) as EditText
        moveFrontAndBack(1, otpEt1, otpEt2, otpEt3, otpEt4,otpEt5,otpEt6)
        //////////////////////////////////////////////////////
        authenticateUser()

    }

    private fun authenticateUser() {
        val phone_number = intent.getStringExtra("Number")
        Log.e("Phone_Number ", phone_number)
        val phoneAuthProvider = PhoneAuthProvider.getInstance()
        phoneAuthProvider.verifyPhoneNumber(
            phone_number,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential?) {
                    signInWithPhoneAuthCredential(credentials!!)
                }

                override fun onVerificationFailed(p0: FirebaseException?) {
                    Toast.makeText(
                        baseContext,
                        " Unable to verify the phone number\n Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(baseContext, PhoneNumberAuthentication::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onCodeSent(verificationID: String?, p1: PhoneAuthProvider.ForceResendingToken?) {

                    Toast.makeText(baseContext, "OTP Sent", Toast.LENGTH_SHORT).show()
                    log_in.setOnClickListener {
//                       etho chnge krna
                               var otpString= otpEt1.text.toString()+otpEt2.text.toString()+otpEt3.text.toString()+otpEt4.text.toString()+otpEt5.text.toString()+otpEt6.text.toString()
                        if (!etOTP.text.toString().isNullOrEmpty()) {
                            val credential =
                                PhoneAuthProvider.getCredential(verificationID!!, etOTP.text.toString())
                            signInWithPhoneAuthCredential(credential)
                        }
                    }
                }
            })
    }

    fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        var firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnSuccessListener {
            val uid = firebaseAuth.currentUser?.uid
            val sharedPreferences = getSharedPreferences("demopref", Context.MODE_PRIVATE)
            val phone_number = intent.getStringExtra("Number")
            sharedPreferences.edit().putString("uid", uid)
                .putBoolean("loginstatus" , false)
                .putString("usertype" , usertype)
                .putString("phonenumber" , phone_number)
                .apply()

            if(usertype == Constants.WORKER) {
                val intent = Intent(baseContext, WorkerMainActivity::class.java)
                startActivity(intent)
            }

            else{
                val intent = Intent(baseContext, EmployerMainActivity::class.java)
                startActivity(intent)
            }
        }.addOnFailureListener {
            Toast.makeText(baseContext, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    //ankur///////////////////////////////////////////////////////////

    fun moveFrontAndBack(mSize: Int, vararg editText: EditText) {
        for (pos in 0 until editText.size - 1) {
            moveToNextEt(editText[pos], editText[pos + 1], mSize)
        }
        for (pos in 1 until editText.size) {
            moveToPreviousEt(editText[pos], editText[pos - 1])
        }
    }

    fun moveToNextEt(mCurrentEt: EditText, mNextEt: EditText, mSize: Int) {
        mCurrentEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mCurrentEt.text.toString().length == mSize) {
                    mNextEt.requestFocus()
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
    fun moveToPreviousEt(mCurrentEt: EditText, mPrevEt: EditText) {
        mCurrentEt.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_DEL
                && keyEvent.action == KeyEvent.ACTION_DOWN
            ) {
                mPrevEt.requestFocus()
            }
            false
        }
    }

}
