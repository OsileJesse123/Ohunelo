package com.jesse.ohunelo.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val SHARED_PREF = "com.jesse.ohunelo.shared_pref"
private const val LOGGED_IN = "com.jesse.ohunelo.logged_in"
private const val FIRST_TIME_USER = "com.jesse.ohunelo.first_time_user"
private const val USER_TYPE = "com.jesse.ohunelo.user_type"
private const val DENIAL_COUNT = "com.jesse.ohunelo.denial_count"

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

    var userType: String?
        set(value) = editor.putString(USER_TYPE, value).apply()
        get() = sharedPreferences.getString(USER_TYPE, null)

    /** Once the user denies permission twice, permission has to be granted manually from the app settings.
        This has to be tracked so as to let the user know that this is the case and grant them the ability
        to go to app settings screen from the app.

        This is done by keep count of how many times permission was denied (if denialCount is equals to 2
        user has permanently denied permission, we need to do the needful.
        )
     */
    var denialCount: Int
        set(value) = editor.putInt(DENIAL_COUNT, value).apply()
        get() = sharedPreferences.getInt(DENIAL_COUNT, 0)
}