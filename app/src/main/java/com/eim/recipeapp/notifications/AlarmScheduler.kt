package com.eim.recipeapp.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.eim.recipeapp.domain.model.MealDetail

object AlarmScheduler {

    fun scheduleAlarm(context: Context, timeInMillis: Long, mealDetail: MealDetail): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // On modern Android, we must have permission to schedule exact alarms.
        if (!alarmManager.canScheduleExactAlarms()) {
            return false // Indicate failure
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_MEAL_ID, mealDetail.mealId)
            putExtra(NotificationReceiver.EXTRA_MEAL_NAME, mealDetail.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            mealDetail.mealId.hashCode(), // Use a unique request code for each alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
        return true // Indicate success
    }
}
