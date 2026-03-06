package fr.onyxleroy.to_do.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.onyxleroy.to_do.Todo;

public class TodoStorageManager {
    private static final String FILE_NAME = "todos.dat";
    private static final int CURRENT_VERSION = 2;

    public static void saveTodos(Context context, List<Todo> todos) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            TodoStorageData data = new TodoStorageData(CURRENT_VERSION, todos);
            oos.writeObject(data);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Todo> loadTodos(Context context) {
        List<Todo> todos = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();
            fis.close();

            if (obj instanceof TodoStorageData) {
                TodoStorageData data = (TodoStorageData) obj;
                return migrateIfNeeded(data.todos, data.version);
            } else if (obj instanceof List) {
                return migrateIfNeeded((List<Todo>) obj, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todos;
    }

    private static List<Todo> migrateIfNeeded(List<Todo> todos, int version) {
        if (version >= CURRENT_VERSION) {
            return todos;
        }
        for (Todo todo : todos) {
            if (todo.getId() == null || todo.getId().isEmpty()) {
                todo.setId(java.util.UUID.randomUUID().toString());
            }
            if (todo.getTags() == null) {
                todo.setTags(new ArrayList<>());
            }
            if (todo.getRepeatType() == null) {
                todo.setRepeatType(Todo.RepeatType.NONE);
            }
        }
        return todos;
    }

    public static boolean importFromFile(Context context, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();
            fis.close();

            List<Todo> importedTodos = null;
            int importedVersion = 1;

            if (obj instanceof TodoStorageData) {
                TodoStorageData data = (TodoStorageData) obj;
                importedTodos = data.todos;
                importedVersion = data.version;
            } else if (obj instanceof List) {
                importedTodos = (List<Todo>) obj;
            }

            if (importedTodos != null) {
                List<Todo> migratedTodos = migrateIfNeeded(importedTodos, importedVersion);
                List<Todo> existingTodos = loadTodos(context);
                for (Todo imported : migratedTodos) {
                    boolean found = false;
                    for (Todo existing : existingTodos) {
                        if (existing.getId() != null && existing.getId().equals(imported.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        existingTodos.add(imported);
                    }
                }
                saveTodos(context, existingTodos);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static class TodoStorageData implements Serializable {
        private static final long serialVersionUID = 1L;
        int version;
        List<Todo> todos;

        TodoStorageData(int version, List<Todo> todos) {
            this.version = version;
            this.todos = todos;
        }
    }
}