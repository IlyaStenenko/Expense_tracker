package com.labs.lab_2_expense_tracker.utils
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.labs.lab_2_expense_tracker.R
import com.labs.lab_2_expense_tracker.data.TransactionStorage
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val storage = TransactionStorage(context)
        val transactions = storage.load()
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        val spent = transactions.filter {
            Calendar.getInstance().apply { timeInMillis = it.timestamp }
                .get(Calendar.MONTH) == currentMonth
        }.sumOf { it.amount }

        val limit = context.getSharedPreferences("limit", Context.MODE_PRIVATE)
            .getFloat("monthly_limit", 1000f)

        if (spent >= limit * 0.8f) {
            val builder = NotificationCompat.Builder(context, "expenses")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Лимит расходов")
                .setContentText("Вы израсходовали ${spent} из $limit")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            // Проверка разрешения на Android 13+
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(1, builder.build())
            }
        }
    }
}

