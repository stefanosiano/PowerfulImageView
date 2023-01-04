package com.stefanosiano.powerful_libraries.imageviewsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stefanosiano.powerful_libraries.imageviewsample.databinding.ActivityShapeBinding

internal class ShapeActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityShapeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShapeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
