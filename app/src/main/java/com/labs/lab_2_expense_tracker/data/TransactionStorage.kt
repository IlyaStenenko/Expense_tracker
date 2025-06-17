package com.labs.lab_2_expense_tracker.data

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class TransactionStorage(context: Context) {
    private val prefs = context.getSharedPreferences("transactions", Context.MODE_PRIVATE)

    fun save(transactions: List<Transaction>) {
        val json = Gson().toJson(transactions)
        prefs.edit().putString("data", json).apply()
    }

    fun load(): MutableList<Transaction> {
        val json = prefs.getString("data", "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun delete(transaction: Transaction) {
        val list = load().toMutableList()
        list.removeAll { it.id == transaction.id }
        save(list)
    }
}

