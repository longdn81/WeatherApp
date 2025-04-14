package com.example.weatherapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash1)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,MainActivity:: class.java)
            startActivity(intent)
            finish()
        } , 3000)
    }
}