package com.labs.lab_2_expense_tracker.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.labs.lab_2_expense_tracker.R
import com.labs.lab_2_expense_tracker.data.LimitStorage
import com.labs.lab_2_expense_tracker.notifications.NotificationScheduler

class LimitSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_limit)

        val limitInput = findViewById<EditText>(R.id.limitInput)
        val saveButton = findViewById<Button>(R.id.saveLimitButton)

        val currentLimit = LimitStorage.getLimit(this)
        if (currentLimit > 0) {
            limitInput.setText(currentLimit.toString())
        }

        saveButton.setOnClickListener {
            val limit = limitInput.text.toString().toDoubleOrNull()
            if (limit != null && limit > 0) {
                LimitStorage.saveLimit(this, limit)
                NotificationScheduler.scheduleLimitCheck(this)
                Toast.makeText(this, "Лимит установлен: $limit ₽", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Введите корректное значение", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
