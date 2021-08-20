package com.thanos.romidownloader.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroPageTransformerType
import com.thanos.romidownloader.MainActivity
import com.thanos.romidownloader.R

class CustomLayoutIntro : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_custom_layout1))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_custom_layout2))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_custom_layout3))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_custom_layout4))
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro_custom_layout5))


//        setProgressIndicator()
        setTransformer(AppIntroPageTransformerType.Parallax())
    }

    public override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)

            intentActivity()


    }

    public override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        intentActivity()

    }


    private fun intentActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
    }
}