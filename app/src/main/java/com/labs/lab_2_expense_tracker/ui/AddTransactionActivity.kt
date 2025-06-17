package com.labs.lab_2_expense_tracker.ui
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.labs.lab_2_expense_tracker.R
import com.labs.lab_2_expense_tracker.data.Transaction
import com.labs.lab_2_expense_tracker.data.TransactionStorage
import com.google.android.material.snackbar.Snackbar

class AddTransactionActivity : ComponentActivity() {
    private lateinit var storage: TransactionStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        storage = TransactionStorage(this)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val categoryInput = findViewById<EditText>(R.id.categoryInput)

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val category = categoryInput.text.toString()

            if (title.isNotBlank() && amount != null && category.isNotBlank()) {
                val newTransaction = Transaction(
                    title = title,
                    amount = amount,
                    category = category
                )

                val list = storage.load()
                list.add(newTransaction)
                Log.d("TX_DEBUG", "saving: $title, $amount, $category; before size=${list.size}")
                storage.save(list)
                Log.d("TX_DEBUG", "after saving, size now = ${storage.load().size}")

                Snackbar.make(it, "Добавлено!", Snackbar.LENGTH_SHORT).show()

                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, "Пожалуйста, введите все поля корректно", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

