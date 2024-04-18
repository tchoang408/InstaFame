package com.example.instafameproj

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.instafameproj.databinding.ActivityMainBinding
import com.example.instafameproj.ui.AuthUser
import com.example.instafameproj.ui.UserProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authUser : AuthUser
    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var navController: NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.user_profile -> {
                    navController.navigate(R.id.action_toUserProfile)
                    true
                }
                R.id.navigation_Upload -> {
                    navController.navigate(R.id.action_toUserUpload)
                    true
                }
                R.id.navigation_home -> {
                    navController.navigate(R.id.action_toUserHome)
                    true
                }
                R.id.Logout -> {
                    authUser.logout()
                    true
                }
                else -> false
            }
        }

    }
    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        // Create authentication object.  This will log the user in if needed
        authUser = AuthUser(activityResultRegistry)
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)
        authUser.observeUser().observe(this) {
            // XXX Write me, user status has changed
            Log.d("username", it.name)
            Log.d("email", it.email)
            Log.d("uid", it.uid)
            if(it.uid != "-1") {
                viewModel.setCurrentAuthUser(it)
                viewModel.addUser(it.name, it.email, it.uid)
            }
        }
    }


}