package com.uts.homelab.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.uts.homelab.MainActivity
import com.uts.homelab.databinding.ActivitySplashAnimatedBinding


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
                startActivity(Intent(this@SplashAnimated, MainActivity::class.java))
                finish()
            }
        }

        countDownTimer.start()
    }
}