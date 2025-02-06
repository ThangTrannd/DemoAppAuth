package test.app.demooidc

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "DemoAppAuth"
    private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAccessToken(context: Context, accessToken: String) {
        getSharedPreferences(context).edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(context: Context): String {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

    fun clearUserSession(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }
}