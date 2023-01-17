package com.jesse.ohunelo.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.ActivityOhuneloBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OhuneloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOhuneloBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Returns an instance of Splash Screen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityOhuneloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.ohunelo_fragment_container)
                as NavHostFragment
        val navController = navHost.navController
        binding.ohuneloBottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.ohunelo_fragment_container)
        return navController.navigateUp()
    }
}