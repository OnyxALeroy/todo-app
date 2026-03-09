package fr.onyxleroy.to_do.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import fr.onyxleroy.to_do.Todo;
import fr.onyxleroy.to_do.utils.NotificationHelper;
import fr.onyxleroy.to_do.utils.TodoStorageManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            List<Todo> todos = TodoStorageManager.loadTodos(context);
            if (todos != null) {
                for (Todo todo : todos) {
                    if (!todo.isCompleted()) {
                        NotificationHelper.scheduleNotification(context, todo);
                    }
                }
            }
        }
    }
}
