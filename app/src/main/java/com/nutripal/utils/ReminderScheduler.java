package com.nutripal.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.nutripal.receivers.ReminderReceiver;

import java.util.Calendar;

public class ReminderScheduler {

    public static final String DEFAULT_REMINDER_SCHEDULE = "09:00 Breakfast, 13:00 Lunch, 15:00 Water, 19:00 Dinner";

    public static void scheduleDefaultReminders(Context context) {
        ReminderReceiver.createChannelIfNeeded(context);

        scheduleDailyReminder(context, 101, 9, 0,
                "Breakfast reminder", "Log your breakfast in NutriPal.");
        scheduleDailyReminder(context, 102, 13, 0,
                "Lunch reminder", "Log your lunch in NutriPal.");
        scheduleDailyReminder(context, 103, 19, 0,
                "Dinner reminder", "Log your dinner in NutriPal.");
        scheduleDailyReminder(context, 104, 15, 0,
                "Water reminder", "Drink water and update your hydration.");
    }

    public static void sendTestReminderNow(Context context) {
        ReminderReceiver.createChannelIfNeeded(context);
        Intent testIntent = new Intent(context, ReminderReceiver.class);
        testIntent.putExtra(ReminderReceiver.EXTRA_TITLE, "NutriPal Test Reminder");
        testIntent.putExtra(ReminderReceiver.EXTRA_MESSAGE, "Daily reminders are enabled successfully.");
        context.sendBroadcast(testIntent);
    }

    private static void scheduleDailyReminder(Context context,
                                              int requestCode,
                                              int hour,
                                              int minute,
                                              String title,
                                              String message) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_TITLE, title);
        intent.putExtra(ReminderReceiver.EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }
}
