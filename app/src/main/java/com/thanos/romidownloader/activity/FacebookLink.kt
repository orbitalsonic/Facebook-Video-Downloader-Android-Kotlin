package com.thanos.romidownloader.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.thanos.romidownloader.R
import com.thanos.romidownloader.downloader.FbVideoDownloader
import com.thanos.romidownloader.utils.BaseObject

class FacebookLink : AppCompatActivity() {

    var edit_Text_web: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook_link)
        supportActionBar?.hide()
        edit_Text_web = findViewById(R.id.edit_Text_web)

    }

    fun myViewClickMethod(view: View) {
        when (view.id) {
            R.id.downloadClick ->{
                val mURL: String = edit_Text_web?.text.toString()
                val facebook = "www.facebook.com"
                if (mURL.isEmpty()) {
                    Toast.makeText(this@FacebookLink, "Please Enter the Link", Toast.LENGTH_SHORT)
                        .show()
                } else {
                        when {
                            mURL.toLowerCase().contains(facebook.toLowerCase()) -> {
                                val downloader =
                                    FbVideoDownloader(this@FacebookLink, mURL)
                                downloader.DownloadVideo()
                                edit_Text_web?.setText("")
//                            showDownloadDialog()
                            }
                            else -> {
                                edit_Text_web?.setText("")
                                Toast.makeText(
                                    this@FacebookLink,
                                    "Invalid Link Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }
            R.id.backicon ->{
                super.onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        val adDialog = Dialog(this)
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adDialog.setCanceledOnTouchOutside(false)
        adDialog.setContentView(R.layout.dialogs_exit)
        adDialog.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val _cancel = adDialog.findViewById<TextView>(R.id._no)
        _cancel.setOnClickListener { adDialog.dismiss() }
        val _ok = adDialog.findViewById<TextView>(R.id._yes)
        _ok.setOnClickListener {
            super@FacebookLink.onBackPressed()
            adDialog.dismiss()
        }
        adDialog.show()
    }

}