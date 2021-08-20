package com.thanos.romidownloader.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.thanos.romidownloader.MainActivity
import com.thanos.romidownloader.R
import com.thanos.romidownloader.utils.BaseObject

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_TIME_OUT = 5000

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            intentMethod()
        }, SPLASH_TIME_OUT.toLong())
    }

    private fun intentMethod(){
        if (BaseObject.getPrivacy(this)) {
            startActivity(Intent(this@SplashActivity, PrivacyPolicy::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}