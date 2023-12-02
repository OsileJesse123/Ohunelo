package com.jesse.ohunelo.presentation.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.facebook.FacebookSdk
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.ActivityMainBinding
import com.jesse.ohunelo.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var previousTranslationY: Float = 0f
    private lateinit var animator: ObjectAnimator

    private var keepShowingSplashScreen = true

    private val sharedViewModel by viewModels<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Returns an instance of Splash Screen
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                keepShowingSplashScreen
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.ohunelo_fragment_container)
                as NavHostFragment
        val navController = navHost.navController
        binding.ohuneloBottomNav.setupWithNavController(navController)

        // Initialize animator object
        animator = ObjectAnimator.ofFloat(binding.ohuneloBottomNav, "translationY", 0f,
            binding.ohuneloBottomNav.height.toFloat())
        animator.duration = 300

        // Add a ViewTreeObserver to get the height of the bottom navigation view when it becomes visible
        binding.ohuneloBottomNav.viewTreeObserver.addOnGlobalLayoutListener {
            addOnDestinationChangedListener(navController)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                sharedViewModel.shouldKeepShowingSplashScreen.collect{
                    if(!it){
                        Handler(Looper.getMainLooper()).postDelayed({ keepShowingSplashScreen = false }, 1_000L)
                    }
                }
            }
        }

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext);
    }

    private fun addOnDestinationChangedListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    showBottomNavigationView()
                }

                R.id.searchRecipeFragment -> {
                    showBottomNavigationView()
                }

                R.id.notificationFragment -> {
                    showBottomNavigationView()
                }

                R.id.profileFragment -> {
                    showBottomNavigationView()
                }
                else -> {
                    hideBottomNavigationView()
                }
            }
        }
    }

    private fun showBottomNavigationView(){
        val translationY = binding.ohuneloBottomNav.translationY
        if (translationY > 0f){
            animateBottomNavigationView(
                binding.ohuneloBottomNav.height.toFloat(),
                0f,
                false
            )
        }
    }

    private fun hideBottomNavigationView(){
        val translationY = binding.ohuneloBottomNav.translationY
        if(translationY == 0f){
            animateBottomNavigationView(
                0f,
                binding.ohuneloBottomNav.height.toFloat(),
                true
            )
        }
    }

    private fun animateBottomNavigationView(from: Float, to: Float, hideBottomNavView: Boolean) {
        if (previousTranslationY != to) {
            animator.cancel()
            animator.setFloatValues(from, to)
            animator.addListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(p0: Animator) {
                    // No implementation needed
                }

                override fun onAnimationEnd(p0: Animator) {
                    if (hideBottomNavView){
                        binding.ohuneloBottomNav.visibility = View.GONE
                    } else {
                        binding.ohuneloBottomNav.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationCancel(p0: Animator) {
                    // No implementation needed
                }

                override fun onAnimationRepeat(p0: Animator) {
                    // No implementation needed
                }

            })
            animator.start()
            previousTranslationY = to
        }
    }

}