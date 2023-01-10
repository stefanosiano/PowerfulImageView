package com.stefanosiano.powerful_libraries.imageviewsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stefanosiano.powerful_libraries.imageviewsample.databinding.ActivityProgressBinding

internal class ProgressActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
