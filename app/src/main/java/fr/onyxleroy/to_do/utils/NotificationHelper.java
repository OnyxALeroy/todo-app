package fr.onyxleroy.to_do.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import fr.onyxleroy.to_do.Todo;
import fr.onyxleroy.to_do.receivers.NotificationReceiver;

public class NotificationHelper {

    public static void scheduleNotification(Context context, Todo todo) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("todo_id", todo.getId());
        intent.putExtra("title", todo.getTitle());
        intent.putExtra("description", todo.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                todo.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            // Cancel any existing alarm for this todo
            alarmManager.cancel(pendingIntent);

            // Only schedule if the time is in the future
            if (todo.getDateTimeMillis() > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                todo.getDateTimeMillis(),
                                pendingIntent
                        );
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            todo.getDateTimeMillis(),
                            pendingIntent
                    );
                }
            }
        }
    }

    public static void cancelNotification(Context context, String todoId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                todoId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}