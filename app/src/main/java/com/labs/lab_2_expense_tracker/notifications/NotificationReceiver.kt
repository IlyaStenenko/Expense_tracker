package com.labs.lab_2_expense_tracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.labs.lab_2_expense_tracker.R
import com.labs.lab_2_expense_tracker.MainActivity
import com.labs.lab_2_expense_tracker.data.TransactionStorage
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val storage = TransactionStorage(context)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)


        val transactions = storage.load()

        val monthlySum = transactions.filter {
            val transCalendar = Calendar.getInstance()
            transCalendar.timeInMillis = it.timestamp
            transCalendar.get(Calendar.MONTH) == currentMonth &&
                    transCalendar.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val limit = prefs.getFloat("monthly_limit", 0f)

        if (limit > 0 && monthlySum >= limit * 0.8 && monthlySum < limit) {
            sendNotification(context, monthlySum, limit)
        }
    }

    private fun sendNotification(context: Context, spent: Double, limit: Float) {
        val channelId = "expense_limit_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Ограничение расходов",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Внимание! Расходы близки к лимиту")
            .setContentText("Вы потратили %.2f из %.2f ₽".format(spent, limit))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
