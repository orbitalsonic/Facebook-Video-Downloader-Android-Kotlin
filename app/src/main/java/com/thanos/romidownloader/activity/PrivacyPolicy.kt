package com.thanos.romidownloader.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.thanos.romidownloader.R
import com.thanos.romidownloader.utils.BaseObject


class PrivacyPolicy : AppCompatActivity() {
    private lateinit var privacyButton: Button
    private lateinit var privacyCheckBox: CheckBox
    private lateinit var privacyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        privacyButton = findViewById(R.id.privacyButton)
        privacyCheckBox = findViewById(R.id.privacyCheckBox)
        privacyTextView = findViewById(R.id.privacyTextView)

        privacyTextView.setOnClickListener(View.OnClickListener {
            val uri =
                Uri.parse(getString(R.string.privacy_url))
            val intent2 = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent2)
        })

        privacyCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                privacyButton.alpha = 1f
            } else {
                privacyButton.alpha = 0.5.toFloat()
            }
        })


        privacyButton.setOnClickListener(View.OnClickListener {
            if (privacyCheckBox.isChecked) {
                BaseObject.setPrivacy(this,false)
                val intent =
                    Intent(this@PrivacyPolicy, CustomLayoutIntro::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"First accept terms and conditions!", Toast.LENGTH_SHORT).show()
            }
        })
    }

}