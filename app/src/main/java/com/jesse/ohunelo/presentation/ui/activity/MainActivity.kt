package com.jesse.ohunelo.presentation.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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

        // Add a ViewTreeObserver to get the height of the bottom navigation view when it becomes visible
        binding.ohuneloBottomNav.viewTreeObserver.addOnGlobalLayoutListener {
            addOnDestinationChangedListener(navController)
        }

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
        Timber.e("Hide Bottom")
        if(translationY == 0f){
            Timber.e("Hide Bottom, i hid it")
            animateBottomNavigationView(
                0f,
                binding.ohuneloBottomNav.height.toFloat(),
                true
            )
        }
    }

    private fun animateBottomNavigationView(from: Float, to: Float, hideBottomNavView: Boolean) {
        Timber.e("Hide Bottom, Bottom Height $to")
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
                        Timber.e("Hide Bottom, is gone")
                    } else {
                        binding.ohuneloBottomNav.visibility = View.VISIBLE
                        Timber.e("Hide Bottom, did not hide")
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