package com.eim.recipeapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eim.recipeapp.MainActivity
import com.eim.recipeapp.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mealName = intent.getStringExtra(EXTRA_MEAL_NAME) ?: "A recipe is waiting for you!"
        val mealId = intent.getStringExtra(EXTRA_MEAL_ID)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recipe Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for recipe reminder notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_MEAL_ID, mealId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            tapIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_recipe_book)
            .setContentTitle("Time to cook! ???")
            .setContentText("Don't forget about your saved recipe: $mealName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        const val CHANNEL_ID = "RECIPE_REMINDER_CHANNEL"
        const val EXTRA_MEAL_NAME = "EXTRA_MEAL_NAME"
        const val EXTRA_MEAL_ID = "EXTRA_MEAL_ID"
    }
}
