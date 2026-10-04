package com.bengkel.app.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.bengkel.app.model.User
import com.bengkel.app.model.UserRole
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bengkel_session_pref", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_ROLE = "user_role"
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_IS_MOCK_MODE = "is_mock_mode"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun saveSession(user: User, token: String?) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_AUTH_TOKEN, token ?: "")
        editor.putString(KEY_ROLE, user.role.name)
        editor.putString(KEY_USER_JSON, gson.toJson(user))
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER_JSON, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserRole(): UserRole {
        val roleStr = prefs.getString(KEY_ROLE, UserRole.CUSTOMER.name)
        return try {
            UserRole.valueOf(roleStr ?: UserRole.CUSTOMER.name)
        } catch (e: Exception) {
            UserRole.CUSTOMER
        }
    }

    fun getToken(): String {
        return prefs.getString(KEY_AUTH_TOKEN, "") ?: ""
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(KEY_IS_LOGGED_IN)
        editor.remove(KEY_AUTH_TOKEN)
        editor.remove(KEY_USER_JSON)
        editor.remove(KEY_ROLE)
        editor.apply()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, url).apply()
    }

    fun getBaseUrl(): String {
        return prefs.getString(KEY_BASE_URL, "http://10.0.2.2:8000/api/") ?: "http://10.0.2.2:8000/api/"
    }

    fun setMockMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_MOCK_MODE, enabled).apply()
    }

    fun isMockMode(): Boolean {
        return prefs.getBoolean(KEY_IS_MOCK_MODE, true) // Default to true so app works immediately offline
    }
}

