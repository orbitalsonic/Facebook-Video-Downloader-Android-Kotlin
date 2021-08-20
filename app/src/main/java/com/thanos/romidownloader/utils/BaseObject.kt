package com.thanos.romidownloader.utils

import android.content.Context

object BaseObject {

    fun getPrivacy(context: Context): Boolean {
        val remoteConfigPref = context.getSharedPreferences("privacyPrefs", Context.MODE_PRIVATE)
        return remoteConfigPref.getBoolean("privacyValue", true)
    }

    fun setPrivacy(context: Context, nativeBanner: Boolean) {
        val remoteConfigPref = context.getSharedPreferences("privacyPrefs", Context.MODE_PRIVATE)
        val sharedPreferencesEditor = remoteConfigPref.edit()
        sharedPreferencesEditor.putBoolean("privacyValue", nativeBanner)
        sharedPreferencesEditor.apply()
    }

}