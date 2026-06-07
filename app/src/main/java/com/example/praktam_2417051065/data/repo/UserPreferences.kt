package com.example.praktam_2417051065.data.repo

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SavedAccount(
    val username: String,
    val email: String,
    val uid: String,
    val type: String, // "EMAIL" or "GOOGLE"
    val password: String? = null
)

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_ACCOUNTS_JSON = "accounts_json"
        private const val KEY_CURRENT_ACCOUNT_JSON = "current_account_json"
    }

    fun getAccounts(): List<SavedAccount> {
        val json = prefs.getString(KEY_ACCOUNTS_JSON, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<SavedAccount>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addAccount(account: SavedAccount) {
        val currentAccounts = getAccounts().toMutableList()
        // Remove existing with same uid or email to avoid duplicates
        currentAccounts.removeAll { it.uid == account.uid || it.email == account.email }
        currentAccounts.add(account)
        val json = gson.toJson(currentAccounts)
        prefs.edit().putString(KEY_ACCOUNTS_JSON, json).apply()
    }

    fun removeAccount(uid: String) {
        val currentAccounts = getAccounts().toMutableList()
        currentAccounts.removeAll { it.uid == uid }
        val json = gson.toJson(currentAccounts)
        prefs.edit().putString(KEY_ACCOUNTS_JSON, json).apply()
        
        val current = getCurrentAccount()
        if (current?.uid == uid) {
            setCurrentAccount(null)
        }
    }

    fun getCurrentAccount(): SavedAccount? {
        val json = prefs.getString(KEY_CURRENT_ACCOUNT_JSON, null) ?: return null
        return try {
            gson.fromJson(json, SavedAccount::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun setCurrentAccount(account: SavedAccount?) {
        if (account == null) {
            prefs.edit().remove(KEY_CURRENT_ACCOUNT_JSON).apply()
        } else {
            val json = gson.toJson(account)
            prefs.edit().putString(KEY_CURRENT_ACCOUNT_JSON, json).apply()
            // Make sure it's in the accounts list
            addAccount(account)
        }
    }
}
