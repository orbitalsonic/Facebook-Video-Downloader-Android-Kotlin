package com.thanos.romidownloader.activity

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.thanos.romidownloader.R
import java.io.File

class FacebookWeb : AppCompatActivity() {

    var myWebView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook_web)
        supportActionBar!!.title = "Facebook"

        myWebView = findViewById<View>(R.id.webview) as WebView
        myWebView!!.settings.javaScriptEnabled = true
        myWebView!!.settings.pluginState = WebSettings.PluginState.ON
        myWebView!!.settings.builtInZoomControls = true
        myWebView!!.settings.displayZoomControls = true
        myWebView!!.settings.useWideViewPort = true
        myWebView!!.settings.loadWithOverviewMode = true
        myWebView!!.addJavascriptInterface(this, "FBDownloader")
//        myWebView.setWebChromeClient(new WebChromeClient());
        //        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                myWebView!!.loadUrl(
                    "javascript:(function() { "
                            + "var el = document.querySelectorAll('div[data-sigil]');"
                            + "for(var i=0;i<el.length; i++)"
                            + "{"
                            + "var sigil = el[i].dataset.sigil;"
                            + "if(sigil.indexOf('inlineVideo') > -1){"
                            + "delete el[i].dataset.sigil;"
                            + "var jsonData = JSON.parse(el[i].dataset.store);"
                            + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');"
                            + "}" + "}" + "})()"
                )
            }

            override fun onLoadResource(view: WebView, url: String) {
                myWebView!!.loadUrl(
                    "javascript:(function prepareVideo() { "
                            + "var el = document.querySelectorAll('div[data-sigil]');"
                            + "for(var i=0;i<el.length; i++)"
                            + "{"
                            + "var sigil = el[i].dataset.sigil;"
                            + "if(sigil.indexOf('inlineVideo') > -1){"
                            + "delete el[i].dataset.sigil;"
                            + "console.log(i);"
                            + "var jsonData = JSON.parse(el[i].dataset.store);"
                            + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                            + "}" + "}" + "})()"
                )
                myWebView!!.loadUrl("javascript:( window.onload=prepareVideo;" + ")()")
            }
        }

        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        myWebView!!.loadUrl("https://www.facebook.com")

    }


    @JavascriptInterface
    fun processVideo(vidData: String, vidID: String) {
//        Toast.makeText(this, "Download Link: " + vidID, Toast.LENGTH_LONG).show();
        showDialog(vidData, vidID)
    }

    override fun onBackPressed() {
        if (myWebView!!.canGoBack()) {
            myWebView!!.goBack()
        } else {
            showExitDialog()
        }
    }


    private fun showDialog(vidData: String, vidID: String) {
        val adDialog = Dialog(this)
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adDialog.setCanceledOnTouchOutside(true)
        adDialog.setContentView(R.layout.dialogs_video)
        adDialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        Log.i("myURL", vidData)
        val watchVideo = adDialog.findViewById<ConstraintLayout>(R.id.watchVideo)
        watchVideo!!.setOnClickListener {
            val intent = Intent(this@FacebookWeb, VideoPlayerFB::class.java)
            intent.putExtra("video_uri", vidData)
            startActivity(intent)
            adDialog.dismiss()
        }
        val downloadVideo =
            adDialog.findViewById<ConstraintLayout>(R.id.downloadVideo)
        downloadVideo!!.setOnClickListener {
            adDialog.dismiss()
            runOnUiThread {
                try {
//                    val mBaseFolderPath = (Environment
//                        .getExternalStorageDirectory()
//                        .toString() + File.separator
//                            + "FacebookDownloader" + File.separator)
//                    if (!File(mBaseFolderPath).exists()) {
//                        File(mBaseFolderPath).mkdir()
//                    }
//                    val mFilePath =
//                        "file://$mBaseFolderPath/facebook_$vidID.mp4"
                    val downloadUri = Uri.parse(vidData)
                    val req = DownloadManager.Request(downloadUri)
//                    req.setDestinationUri(Uri.parse(mFilePath))
                    req.addRequestHeader("Accept", "application/mp4")
                    req.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        File.separator
                                + "FBDownloader" + File.separator + "facebook_" + vidID + ".mp4"
                    )
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    val dm =
                        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(req)
                    showDownloadDialog()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@FacebookWeb,
                        "Video Can't be downloaded! Try Again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        adDialog.show()
    }

    private fun showExitDialog() {
        val adDialog = Dialog(this)
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adDialog.setCanceledOnTouchOutside(false)
        adDialog.setContentView(R.layout.dialogs_exit)
        adDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val _cancel = adDialog.findViewById<TextView>(R.id._no)
        _cancel.setOnClickListener { adDialog.dismiss() }
        val _ok = adDialog.findViewById<TextView>(R.id._yes)
        _ok.setOnClickListener {
            super@FacebookWeb.onBackPressed()
            adDialog.dismiss()
        }
        adDialog.show()
    }

    private fun showDownloadDialog() {
        val adDialog = Dialog(this)
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adDialog.setCanceledOnTouchOutside(true)
        adDialog.setContentView(R.layout.dialog_download)
        adDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val _gotit = adDialog.findViewById<TextView>(R.id._gotit)
        _gotit.setOnClickListener { adDialog.dismiss() }

        adDialog.show()
    }
}