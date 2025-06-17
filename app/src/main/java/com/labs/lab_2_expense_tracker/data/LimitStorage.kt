package com.labs.lab_2_expense_tracker.data

import android.content.Context

object LimitStorage {
    private const val PREF_NAME = "limit_prefs"
    private const val KEY_LIMIT = "monthly_limit"

    fun saveLimit(context: Context, limit: Double) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_LIMIT, limit.toFloat()).apply()
    }

    fun getLimit(context: Context): Double {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_LIMIT, 0f).toDouble()
    }
}