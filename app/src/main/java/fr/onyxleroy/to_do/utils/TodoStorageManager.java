package fr.onyxleroy.to_do.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.onyxleroy.to_do.Todo;

public class TodoStorageManager {
    private static final String FILE_NAME = "todos.dat";

    public static void saveTodos(Context context, List<Todo> todos) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(todos);
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
            todos = (List<Todo>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            // File doesn't exist or error reading - return empty list
            e.printStackTrace();
        }
        return todos;
    }
}