package com.example.photoswitcher.utils

import android.content.Context
import android.content.SharedPreferences

class PrefHelper private constructor(val context: Context) {
    private var sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object : SingletonHolder<PrefHelper, Context>(::PrefHelper) {
        const val PREF_NAME = "photo_exact_pref"
        const val PREF_KEY_INDEX = "pref_key_index"
        val listFileName = listOf("125%402.zip", "127%402.zip")
    }

    fun getLatestIndex(): Int {
        return sharedPref.getInt(PREF_KEY_INDEX, -1)
    }

    fun setLatestIndex(newIndex: Int) {
        with(sharedPref.edit()) {
            putInt(PREF_KEY_INDEX, newIndex)
            apply()
        }
    }
}