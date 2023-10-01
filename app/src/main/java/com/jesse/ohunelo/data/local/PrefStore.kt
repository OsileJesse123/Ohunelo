package com.jesse.ohunelo.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val SHARED_PREF = "com.jesse.ohunelo.shared_pref"
const val LOGGED_IN = "com.jesse.ohunelo.logged_in"
const val FIRST_TIME_USER = "com.jesse.ohunelo.first_time_user"

@Singleton
class PrefStore @Inject constructor(
    @ApplicationContext
    private val context: Context
){

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var isLoggedIn: Boolean
        set(value) = editor.putBoolean(LOGGED_IN, value).apply()
        get() = sharedPreferences.getBoolean(LOGGED_IN, false)

    var isFirstTimeUser: Boolean
        set(value) = editor.putBoolean(FIRST_TIME_USER, value).apply()
        get() = sharedPreferences.getBoolean(FIRST_TIME_USER, true)
}