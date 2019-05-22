package com.example.viewpager.Fragments

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demoapp.R
import kotlinx.android.synthetic.main.a_fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Spanned
import android.text.SpannableString
import android.text.style.ClickableSpan


class FragmentC : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.example.demoapp.R.layout.a_fragment , container , false)
    }



}