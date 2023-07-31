package com.uts.homelab.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uts.homelab.R
import com.uts.homelab.databinding.ActivityNurseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NurseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNurseBinding
    //private val viewModel:NurseViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNurseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_nurse)

        navView.setupWithNavController(navController)
    }

    fun isViewBottomNavigation(bool:Boolean){
        binding.navView.visibility = if(bool){
            View.VISIBLE
        }else{
            View.GONE
        }
    }
}