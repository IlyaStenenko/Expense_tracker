package com.labs.lab_2_expense_tracker.data

import java.util.UUID

import java.io.Serializable

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var amount: Double,
    var category: String,
    var timestamp: Long = System.currentTimeMillis()
) : Serializable
