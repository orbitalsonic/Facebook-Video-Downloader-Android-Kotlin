package com.thanos.romidownloader

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.thanos.romidownloader.activity.CustomLayoutIntro
import com.thanos.romidownloader.activity.DownloadedVideos
import com.thanos.romidownloader.activity.FacebookLink
import com.thanos.romidownloader.activity.FacebookWeb
import com.thanos.romidownloader.utils.BaseObject
import com.thanos.romidownloader.utils.PermissionUtil

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val PERMISSION_REQUEST_CODE_EXT_STORAGE = 10
    private var intentCode = -1

    var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.drawicon -> drawerLayout?.openDrawer(GravityCompat.START)
            R.id.nav_home -> {
            }
            R.id.nav_share -> try {
                val link = Intent(Intent.ACTION_SEND)
                link.type = "text/plain"
                link.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                val sAux = "https://play.google.com/store/apps/details?id=$packageName"
                link.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(link, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
            R.id.nav_rateus -> try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.packageName)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                    )
                )
            }

            R.id.nav_privacy_policy -> startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(getString(R.string.privacy_url))
                )
            )

            R.id.nav_more -> {
                val uri4 = Uri.parse("https://play.google.com/store/apps/developer?id=WALRUS+TECH")
                val intent2 = Intent(Intent.ACTION_VIEW, uri4)
                startActivity(intent2)
            }

        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START)!!) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            showExitDialog()
        }
    }

    fun clickMethod(view: View) {
        when (view.id) {
            R.id.drawicon -> drawerLayout?.openDrawer(GravityCompat.START)
            R.id.facebookConstraint -> {
                intentCode = 0
                if (!PermissionUtil.hasPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    requestPermission()
                } else {
                    startActivity(Intent(this, FacebookWeb::class.java))
                }
            }
            R.id.urlConstraint -> {
                intentCode = 1
                if (!PermissionUtil.hasPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    requestPermission()
                } else {
                    startActivity(Intent(this, FacebookLink::class.java))
                }
            }
            R.id.downloadConstraint -> {
                intentCode = 2
                if (!PermissionUtil.hasPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    requestPermission()
                } else {
                    startActivity(Intent(this, DownloadedVideos::class.java))
                }
            }
            R.id.helpConstraint -> {
                startActivity(Intent(this, CustomLayoutIntro::class.java))
                finish()
            }
        }
    }

    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        PermissionUtil.requestPermissions(
            this,
            permissions, PERMISSION_REQUEST_CODE_EXT_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE_EXT_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                when (intentCode) {
                    0 -> {
                        startActivity(Intent(this@MainActivity, FacebookWeb::class.java))
                    }
                    1 -> {
                        startActivity(Intent(this, FacebookLink::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this, DownloadedVideos::class.java))

                    }


                }
            } else {
                // Permission denied, show rational
                if (PermissionUtil.shouldShowRational(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Access required")
                    builder.setMessage("Permission is required for store downloaded file")

                    builder.setPositiveButton("OK") { dialog, which ->
                        requestPermission()
                    }

                    builder.setNegativeButton("Cancel") { dialog, which ->

                    }


                    builder.show()


                } else {
                    // Exit maybe?
                }
            }
        }
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
            super@MainActivity.onBackPressed()
            adDialog.dismiss()
        }
        adDialog.show()
    }

}