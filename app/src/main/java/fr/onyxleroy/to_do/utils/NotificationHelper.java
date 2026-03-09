package fr.onyxleroy.to_do.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.List;

import fr.onyxleroy.to_do.Todo;
import fr.onyxleroy.to_do.receivers.NotificationReceiver;

public class NotificationHelper {

    public static void scheduleNotification(Context context, Todo todo) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("todo_id", todo.getId());
        intent.putExtra("title", todo.getTitle());
        intent.putExtra("description", todo.getDescription());
        intent.putExtra("repeat_type", todo.getRepeatType() != null ? todo.getRepeatType().getValue() : 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                todo.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);

            long triggerTime = todo.getDateTimeMillis();
            if (triggerTime > System.currentTimeMillis()) {
                setExactAlarm(alarmManager, triggerTime, pendingIntent);
            } else if (todo.isRepeating()) {
                long now = System.currentTimeMillis();
                while (triggerTime <= now) {
                    triggerTime = getNextOccurrence(triggerTime, todo.getRepeatType());
                }
                todo.setDateTimeMillis(triggerTime);
                
                List<Todo> allTodos = TodoStorageManager.loadTodos(context);
                if (allTodos != null) {
                    for (int i = 0; i < allTodos.size(); i++) {
                        if (allTodos.get(i).getId().equals(todo.getId())) {
                            allTodos.set(i, todo);
                            break;
                        }
                    }
                    TodoStorageManager.saveTodos(context, allTodos);
                }
                
                setExactAlarm(alarmManager, triggerTime, pendingIntent);
            }
        }
    }

    public static long getNextOccurrence(long currentTimeMillis, Todo.RepeatType repeatType) {
        if (repeatType == null || repeatType == Todo.RepeatType.NONE) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);

        switch (repeatType) {
            case HOURLY:
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case DAILY:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, 1);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, 1);
                break;
            default:
                return 0;
        }

        return calendar.getTimeInMillis();
    }

    public static void rescheduleNotification(Context context, Todo todo) {
        if (todo.isRepeating()) {
            long nextOccurrence = getNextOccurrence(todo.getDateTimeMillis(), todo.getRepeatType());
            if (nextOccurrence > 0) {
                todo.setDateTimeMillis(nextOccurrence);
                scheduleNotification(context, todo);
            }
        }
    }

    private static void setExactAlarm(AlarmManager alarmManager, long triggerTime, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
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
