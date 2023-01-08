package com.stefanosiano.powerful_libraries.imageviewsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stefanosiano.powerful_libraries.imageviewsample.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
