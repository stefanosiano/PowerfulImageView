package com.stefanosiano.powerful_libraries.imageviewsample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.stefanosiano.powerful_libraries.imageview.PowerfulImageView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)
/*
        piv1less.setOnClickListener { onMinus(piv1, piv1txt) }
        piv2less.setOnClickListener { onMinus(piv2, piv2txt) }
        piv3less.setOnClickListener { onMinus(piv3, piv3txt) }
        piv4less.setOnClickListener { onMinus(piv4, piv4txt) }

        piv1plus.setOnClickListener { onPlus(piv1, piv1txt) }
        piv2plus.setOnClickListener { onPlus(piv2, piv2txt) }
        piv3plus.setOnClickListener { onPlus(piv3, piv3txt) }
        piv4plus.setOnClickListener { onPlus(piv4, piv4txt) }*/
    }

    private fun onPlus(piv: PowerfulImageView, pivtxt: TextView) {
        val lastRadius = piv.getBlurRadius()
        val time = System.currentTimeMillis()
        piv.setBlurRadius(piv.getBlurRadius() + 1)
        piv.getShapeOptions().backgroundColor
        val time2 = System.currentTimeMillis()
        pivtxt.text = "${pivtxt.text}\nRadius $lastRadius -> ${piv.getBlurRadius()}. Time: ${time2-time} ms"
    }

    private fun onMinus(piv: PowerfulImageView, pivtxt: TextView) {
        val lastRadius = piv.getBlurRadius()
        val time = System.currentTimeMillis()
        piv.setBlurRadius(piv.getBlurRadius() - 1)
        val time2 = System.currentTimeMillis()
        pivtxt.text = "${pivtxt.text}\nRadius $lastRadius -> ${piv.getBlurRadius()}. Time: ${time2-time} ms"
    }
}