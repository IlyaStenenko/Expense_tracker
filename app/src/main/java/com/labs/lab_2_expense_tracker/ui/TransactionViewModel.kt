package com.labs.lab_2_expense_tracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.labs.lab_2_expense_tracker.data.Transaction
import com.labs.lab_2_expense_tracker.data.TransactionStorage

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = TransactionStorage(application)
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        _transactions.value = storage.load()
    }

    fun addTransaction(tx: Transaction) {
        val current = storage.load()
        current.add(tx)
        storage.save(current)
        _transactions.value = current
    }

    fun deleteTransaction(tx: Transaction) {
        val current = storage.load().filter { it.id != tx.id }
        storage.save(current)
        _transactions.value = current
    }

    fun updateTransaction(updated: Transaction) {
        val current = storage.load().map {
            if (it.id == updated.id) updated else it
        }
        storage.save(current)
        _transactions.value = current
    }
}
