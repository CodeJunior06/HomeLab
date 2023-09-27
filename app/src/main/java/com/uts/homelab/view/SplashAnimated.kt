package com.uts.homelab.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.databinding.ActivitySplashAnimatedBinding
import com.uts.homelab.utils.datastore.DataStoreManager
import com.uts.homelab.utils.extension.intentToAdminHome
import com.uts.homelab.utils.extension.intentToMain
import com.uts.homelab.utils.extension.intentToNurseHome
import com.uts.homelab.utils.extension.intentToUserHome
import com.uts.homelab.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Singleton

@AndroidEntryPoint
class SplashAnimated : AppCompatActivity() {
    private lateinit var bindingSplashAnimatedBinding: ActivitySplashAnimatedBinding


    @Singleton
    var dataStoreManager: DataStoreManager? = DataStoreManager(this)

    private val viewModel: MainViewModel by viewModels()

    private var time = 0
    private var case = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSplashAnimatedBinding = ActivitySplashAnimatedBinding.inflate(layoutInflater)
        setContentView(bindingSplashAnimatedBinding.root)

        val countDownTimer: CountDownTimer = object : CountDownTimer(time.toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds = millisUntilFinished / 1000
                Log.d("TEMPORIZER", "Tiempo empleado: $seconds segundos")
            }

            override fun onFinish() {
                lifecycleScope.launch {
                   when(case){
                       -1 -> intentToMain()
                       1 -> intentToUserHome()
                       2 -> intentToNurseHome()
                       3 -> intentToAdminHome()
                        else -> print("")
                   }
                }
            }


        }

        lifecycleScope.launch {
            val res = viewModel.isGetNewInstall().firstOrNull()
            if (res == null || res == false) {
                viewModel.isSetNewInstall(false)
                FirebaseAuth.getInstance().signOut()
                time = 3500
                countDownTimer.start()
            } else {

                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                if (currentUser == null) {
                    FirebaseAuth.getInstance().signOut()
                    time = 3500
                    countDownTimer.start()
                } else {
                    viewModel.isSetPetitionParallel(currentUser.email!!,false)
                }
            }
        }

        viewModel.isProgress.observe(this){
            if(!it.first){
                case = it.second
                time = 2500
                countDownTimer.start()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        dataStoreManager = null
    }
}