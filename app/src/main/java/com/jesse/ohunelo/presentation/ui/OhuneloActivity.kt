package com.jesse.ohunelo.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.ActivityOhuneloBinding

class OhuneloActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOhuneloBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Returns an instance of Splash Screen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityOhuneloBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}