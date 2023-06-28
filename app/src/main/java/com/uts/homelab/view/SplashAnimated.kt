package com.uts.homelab.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.uts.homelab.databinding.ActivitySplashAnimatedBinding
import com.uts.homelab.utils.extension.intentToUserHome
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashAnimated : AppCompatActivity() {
    private lateinit var bindingSplashAnimatedBinding: ActivitySplashAnimatedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSplashAnimatedBinding = ActivitySplashAnimatedBinding.inflate(layoutInflater)
        setContentView(bindingSplashAnimatedBinding.root)

        val countDownTimer: CountDownTimer = object : CountDownTimer(3500, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds = millisUntilFinished / 1000
                Log.d("TEMPORIZER", "Tiempo empleado: $seconds segundos")
            }
            
            override fun onFinish() {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                if (currentUser != null) {
                    intentToUserHome()
                }else{
                    startActivity(Intent(this@SplashAnimated, MainActivity::class.java))
                }
                finish()
            }
        }

        countDownTimer.start()
    }
}