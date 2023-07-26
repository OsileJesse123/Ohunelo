package com.jesse.ohunelo.presentation.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var previousTranslationY: Float = 0f
    private lateinit var animator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        // Returns an instance of Splash Screen
        val splashScreen = installSplashScreen()
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

        navController.addOnDestinationChangedListener{
            _, destination, _ ->
            when(destination.id){
                R.id.homeFragment -> {
                    showBottomNavigationView()
                }
                R.id.searchRecipeFragment -> {
                    showBottomNavigationView()
                }
                R.id.notificationFragment ->{
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

                override fun onAnimationStart(animation: Animator?) {
                    // No implementation needed
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (hideBottomNavView){
                        binding.ohuneloBottomNav.visibility = View.GONE
                    } else {
                        binding.ohuneloBottomNav.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                    // No implementation needed
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    // No implementation needed
                }

            })
            animator.start()
            previousTranslationY = to
        }
    }

}