package com.youtubevideos.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.youtubevideos.R

class ActSplash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ActHome::class.java))
            finish()
        }, 1000)

    }

}