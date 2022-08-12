package com.stefanosiano.powerful_libraries.imageviewsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stefanosiano.powerful_libraries.imageviewsample.databinding.ActivityBlurBinding

class BlurActivity : AppCompatActivity() {

    lateinit var binding : ActivityBlurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
